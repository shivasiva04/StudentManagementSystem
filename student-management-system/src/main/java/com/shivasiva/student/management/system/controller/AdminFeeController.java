package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.AdminAllottedFee;
import com.shivasiva.student.management.system.model.Fee;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.service.FeeService;
import com.shivasiva.student.management.system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/fees")
public class AdminFeeController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FeeService feeService;

    @GetMapping
    public String viewFees(@RequestParam(required = false) String department,
                           @RequestParam(required = false) String semester,
                           Model model) {
        List<Student> students;
        List<Fee> fees;
        List<AdminAllottedFee> allottedFees;

        if (department != null && semester != null) {
            students = studentService.getStudentsByDeptAndSem(department, semester);
            fees = feeService.getFeesByDepartmentAndSemester(department, semester);
            allottedFees = feeService.getAllottedFeesByDeptAndSem(department, semester);
        } else {
            students = studentService.getAllStudents();
            fees = feeService.getAllFees();
            allottedFees = feeService.getAllAllottedFees();
        }

        model.addAttribute("departments", studentService.getAllDepartments()); // ✅ ADD THIS
        model.addAttribute("students", students);
        model.addAttribute("fees", fees);
        model.addAttribute("allottedFees", allottedFees);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedSemester", semester);

        return "admin/payments";
    }




    @PostMapping("/add")
    public String addFeeToAllStudents(@RequestParam String department,
                                      @RequestParam String semester,
                                      @RequestParam double amount,
                                      @RequestParam String paymentDate,
                                      RedirectAttributes redirectAttributes) {
        feeService.addFeeToAllStudents(department, semester, amount, paymentDate);
        redirectAttributes.addFlashAttribute("successMessage", "Payment allotted successfully!");
        return "redirect:/admin/fees";
    }


    @GetMapping("/students/filter")
    @ResponseBody
    public List<Student> filterStudents(@RequestParam String department,
                                        @RequestParam String semester) {
        return studentService.getStudentsByDeptAndSem(department, semester);
    }
}

