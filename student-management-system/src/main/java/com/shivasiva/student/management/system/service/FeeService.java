package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.AdminAllottedFee;
import com.shivasiva.student.management.system.model.Fee;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.repository.AdminAllottedFeeRepository;
import com.shivasiva.student.management.system.repository.FeeRepository;
import com.shivasiva.student.management.system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AdminAllottedFeeRepository adminAllottedFeeRepository;


    public void addFee(String studentId, double amount, String paymentDate) {
        Fee fee = new Fee();
        fee.setAmount(amount);
        fee.setPaymentDate(LocalDate.parse(paymentDate));
        fee.setStudent(studentRepository.findById(studentId).orElse(null));
        fee.setPaid(false); // Default as unpaid
        feeRepository.save(fee);
    }

    // Add fee to all students in a department & semester + record in AdminAllottedFee
    public void addFeeToAllStudents(String department, String semester, double amount, String paymentDateStr) {
        List<Student> students = studentService.getStudentsByDeptAndSem(department, semester);
        LocalDate paymentDate = LocalDate.parse(paymentDateStr);

        // Save fees for each student
        for (Student student : students) {
            Fee fee = new Fee();
            fee.setStudent(student);
            fee.setAmount(amount);
            fee.setPaymentDate(paymentDate);
            fee.setPaid(false);
            feeRepository.save(fee);
        }

        // Save a record of admin-allotted fee
        AdminAllottedFee adminAllot = new AdminAllottedFee();
        adminAllot.setDepartment(department);
        adminAllot.setSemester(semester);
        adminAllot.setAmount(amount);
        adminAllot.setPaymentDate(paymentDate);
        adminAllottedFeeRepository.save(adminAllot);
    }

    //  Get all admin-allotted fees
    public List<AdminAllottedFee> getAllAllottedFees() {
        return adminAllottedFeeRepository.findAll();
    }

    //  Get allotted fees for specific department and semester
    public List<AdminAllottedFee> getAllottedFeesByStudentDeptAndSem(Student student, String semester) {
        String normalizedSemester = normalizeSemester(semester);
        return adminAllottedFeeRepository.findByDepartmentAndSemester(student.getDepartment(), normalizedSemester);
    }

    private String normalizeSemester(String semester) {
        return semester.trim().replaceAll("[^0-9]", "");
    }


    //  Get fees for all students in a department and semester
    public List<Fee> getFeesByDepartmentAndSemester(String department, String semester) {
        if (department != null && semester != null) {
            return feeRepository.findByStudent_DepartmentAndStudent_Semester(department, semester);
        } else {
            return feeRepository.findAll();
        }
    }

    //  Get all fees for a specific student
    public List<Fee> getFeesByStudent(Student student) {
        return feeRepository.findAll().stream()
                .filter(fee -> fee.getStudent().getId().equals(student.getId()))
                .collect(Collectors.toList());
    }

    //  Get a specific fee by ID
    public Optional<Fee> getFeeById(Long id) {
        return feeRepository.findById(id);
    }

    //  Save/Update a fee record
    public void saveFee(Fee fee) {
        feeRepository.save(fee);
    }

    //  Get all student fee records
    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public boolean markFeeAsPaid(Long feeId) {
        Optional<Fee> optionalFee = feeRepository.findById(feeId);
        if (optionalFee.isPresent()) {
            Fee fee = optionalFee.get();

            //  Only mark unpaid fees as paid (avoid re-paying already paid ones)
            if (!fee.isPaid()) {
                fee.setPaid(true);
                fee.setPaymentDate(LocalDate.now()); // Mark current date as payment date
                feeRepository.save(fee);
                return true;
            }
        }
        return false;
    }


    public List<Fee> getUnpaidFeesByStudent(Student student) {
        return feeRepository.findByStudentAndPaid(student, false);
    }

    public List<Fee> getPaidFeesByStudent(Student student) {
        return feeRepository.findByStudentAndPaid(student, true);
    }

//    public List<AdminAllottedFee> getAllottedFeesByStudentDeptAndSem(Student student, String semester) {
//        return adminAllottedFeeRepository.findByDepartmentAndSemester(student.getDepartment(), semester);
//    }


    public boolean markAllottedFeeAsPaid(Long feeId) {
        Optional<AdminAllottedFee> optionalFee = adminAllottedFeeRepository.findById(feeId);
        if (optionalFee.isPresent()) {
            AdminAllottedFee fee = optionalFee.get();
            if (fee.getPaymentDate() == null) {
                fee.setPaymentDate(LocalDate.now());
                adminAllottedFeeRepository.save(fee);
                return true;
            }
        }
        return false;
    }

    public List<AdminAllottedFee> getAllottedFeesByDeptAndSem(String department, String semester) {
        return adminAllottedFeeRepository.findByDepartmentAndSemester(department, semester);
    }

    public Optional<AdminAllottedFee> getAllottedFeeById(Long id) {
        return adminAllottedFeeRepository.findById(id);
    }

    public void saveAllottedFee(AdminAllottedFee fee) {
        adminAllottedFeeRepository.save(fee);
    }

    public void saveStudentFee(Fee fee) {
        feeRepository.save(fee);
    }
    //  Get all allotted fees for student's department (used when no semester is selected)
    public List<AdminAllottedFee> getAllottedFeesByStudentDept(Student student) {
        return adminAllottedFeeRepository.findByDepartment(student.getDepartment());
    }

}
