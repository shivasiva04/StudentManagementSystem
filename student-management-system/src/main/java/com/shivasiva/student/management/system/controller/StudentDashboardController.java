package com.shivasiva.student.management.system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivasiva.student.management.system.model.*;
import com.shivasiva.student.management.system.repository.AttendanceRepository;
import com.shivasiva.student.management.system.repository.CourseRepository;
import com.shivasiva.student.management.system.repository.StudentRepository;
import com.shivasiva.student.management.system.service.AttendanceService;
import com.shivasiva.student.management.system.service.FeeService;
import com.shivasiva.student.management.system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentDashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private StudentService studentService;


    @GetMapping("/student/dashboard")
    public String studentDashboard(@RequestParam(value = "filteredSemester", required = false) String filteredSemester,
                                   Authentication authentication, Model model) throws JsonProcessingException {

        String loginValue = authentication.getName();
        System.out.println("🔐 Logged in value: " + loginValue);

        Optional<Student> optionalStudent = studentRepository.findByEmail(loginValue);
        if (optionalStudent.isEmpty()) {
            optionalStudent = studentRepository.findByName(loginValue);
        }

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            List<Fee> paidFees = feeService.getPaidFeesByStudent(student);
            List<Fee> unpaidFees = feeService.getUnpaidFeesByStudent(student);

            double paidAmount = paidFees.stream().mapToDouble(Fee::getAmount).sum();
            double unpaidAmount = unpaidFees.stream().mapToDouble(Fee::getAmount).sum();
            double totalFee = paidAmount + unpaidAmount;



            model.addAttribute("paidFees", paidFees);
            model.addAttribute("unpaidFees", unpaidFees);
            model.addAttribute("paidAmount", paidAmount);
            model.addAttribute("unpaidAmount", unpaidAmount);
            model.addAttribute("totalFee", totalFee);



            List<Course> departmentCourses = courseRepository.findByDepartment(student.getDepartment());

            Set<String> uniqueSemesters = new LinkedHashSet<>();
            if (departmentCourses != null && !departmentCourses.isEmpty()) {
                uniqueSemesters = departmentCourses.stream()
                        .map(Course::getSemester)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }


            String defaultSemester = (filteredSemester != null && !filteredSemester.isEmpty())
                    ? filteredSemester
                    : uniqueSemesters.stream().findFirst().orElse("1");
            List<AdminAllottedFee> filteredAllottedFees = feeService.getAllottedFeesByStudentDeptAndSem(student, defaultSemester);

            System.out.println("📋 Fetched Allotted Fees for Dept: " + student.getDepartment() + ", Semester: " + defaultSemester);
            System.out.println("📦 Total AdminAllottedFees found: " + filteredAllottedFees.size());

            filteredAllottedFees.forEach(fee -> {
                System.out.println("➡️ Fee: Dept=" + fee.getDepartment() + ", Sem=" + fee.getSemester() + ", Amt=" + fee.getAmount());
            });

            model.addAttribute("filteredAllottedFees", filteredAllottedFees);

            List<Fee> studentPaidFees = feeService.getPaidFeesByStudent(student);

            Map<Long, Boolean> allottedFeePaidStatus = new HashMap<>();
            for (AdminAllottedFee allottedFee : filteredAllottedFees) {
                boolean isPaid = studentPaidFees.stream().anyMatch(fee ->
                        fee.getAmount() == allottedFee.getAmount()
                                && fee.getStudent().getId().equals(student.getId())
                                && fee.getStudent().getSemester().equalsIgnoreCase(allottedFee.getSemester())
                                && fee.getStudent().getDepartment().equalsIgnoreCase(allottedFee.getDepartment())
                );


                allottedFeePaidStatus.put(allottedFee.getId(), isPaid);
            }
            model.addAttribute("allottedFeePaidStatus", allottedFeePaidStatus);


            model.addAttribute("selectedSemester", defaultSemester);

            List<Course> enrolledCourses = student.getCourses();

            Map<Long, List<Attendance>> detailedAttendanceMap = new HashMap<>();
            Map<Long, Double> attendancePercentages = new HashMap<>();
            double totalPercentage = 0.0;

            for (Course course : enrolledCourses) {
                List<Attendance> attendanceList = attendanceRepository.findByStudentAndCourse(student, course);
                attendanceList.sort(Comparator.comparing(Attendance::getDate).reversed());
                detailedAttendanceMap.put(course.getId(), attendanceList);

                try {
                    double percentage = attendanceService.getAttendancePercentageByCourse(student, course);
                    int rounded = (int) Math.round(percentage);
                    attendancePercentages.put(course.getId(), (double) rounded);
                    totalPercentage += rounded;

                } catch (Exception e) {
                    System.out.println("⚠️ Error calculating attendance for course: " + course.getName());
                    e.printStackTrace();
                    attendancePercentages.put(course.getId(), 0.0);
                }
            }

            double overallPercentage = enrolledCourses.isEmpty() ? 0.0 : totalPercentage / enrolledCourses.size();



            model.addAttribute("detailedAttendanceMap", detailedAttendanceMap);
            model.addAttribute("attendancePercentages", attendancePercentages);
            model.addAttribute("overallAttendance", overallPercentage);

            //  Get daily attendance for current month
            LocalDate today = LocalDate.now();
            Map<String, Double> dailyAttendanceMap = attendanceService.getDailyAttendanceForMonth(student, today.getMonth(), today.getYear());

            List<String> sortedDates = new ArrayList<>(dailyAttendanceMap.keySet());
            List<Double> dailyPercentages = sortedDates.stream()
                    .map(dailyAttendanceMap::get)
                    .collect(Collectors.toList());

           //  Send to view
            model.addAttribute("dailyLabels", objectMapper.writeValueAsString(sortedDates));
            model.addAttribute("dailyData", objectMapper.writeValueAsString(dailyPercentages));


            model.addAttribute("student", student);
            model.addAttribute("studentName", student.getName());
            model.addAttribute("courses", departmentCourses);
            model.addAttribute("uniqueSemesters", uniqueSemesters);
            model.addAttribute("enrolledCourses", enrolledCourses);

            model.addAttribute("coursesJson", objectMapper.writeValueAsString(departmentCourses));
            model.addAttribute("enrolledCoursesJson", objectMapper.writeValueAsString(
                    enrolledCourses.stream().map(Course::getCode).collect(Collectors.toSet())
            ));

        } else {
            System.out.println("⚠️ Student not found for: " + loginValue);
            model.addAttribute("student", new Student());
            model.addAttribute("studentName", "Guest");
            model.addAttribute("enrolledCourses", List.of());
            model.addAttribute("courses", List.of());
            model.addAttribute("uniqueSemesters", Set.of());
            model.addAttribute("coursesJson", "[]");
            model.addAttribute("enrolledCoursesJson", "[]");
            model.addAttribute("attendancePercentages", new HashMap<Long, Double>());
        }

        return "dashboard/student-dashboard";
    }

    @PostMapping("/student/enroll")
    public String enrollCourse(@RequestParam("courseCode") String courseCode, Authentication authentication) {
        String loginValue = authentication.getName();

        Optional<Student> optionalStudent = studentRepository.findByEmail(loginValue);
        if (optionalStudent.isEmpty()) {
            optionalStudent = studentRepository.findByName(loginValue);
        }

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Course course = courseRepository.findByCode(courseCode);

            if (course == null) {
                System.out.println("⚠️ Course not found for code: " + courseCode);
                return "redirect:/student/dashboard?error=course_not_found";
            }

            List<Course> enrolledCourses = student.getCourses();

            if (!enrolledCourses.contains(course)) {
                enrolledCourses.add(course);
                student.setCourses(enrolledCourses);
                studentRepository.save(student);
                System.out.println("✅ Successfully enrolled in course: " + courseCode);
                return "redirect:/student/dashboard?success=enrolled";
            } else {
                System.out.println("ℹ️ Already enrolled: " + courseCode);
                return "redirect:/student/dashboard?info=already_enrolled";
            }
        }

        System.out.println("⚠️ Student not found: " + loginValue);
        return "redirect:/student/dashboard?error=student_not_found";
    }

    @GetMapping("/student/fees")
    public String showStudentFees(@RequestParam(value = "filteredSemester", required = false) String filteredSemester,
                                  Model model, Authentication auth) {

        Optional<Student> optionalStudent = studentService.getStudentByLogin(auth.getName());

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            // Get all courses in student’s department for semester dropdown
            List<Course> departmentCourses = courseRepository.findByDepartment(student.getDepartment());
            Set<String> uniqueSemesters = departmentCourses.stream()
                    .map(Course::getSemester)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            //  Updated: Preserve selected "All" (empty string) instead of defaulting to "1"
            String selectedSemester = (filteredSemester != null) ? filteredSemester : "";

            //  Fetch allotted fees based on actual selectedSemester
            List<AdminAllottedFee> filteredAllottedFees = (selectedSemester.isEmpty())
                    ? feeService.getAllottedFeesByStudentDept(student)
                    : feeService.getAllottedFeesByStudentDeptAndSem(student, selectedSemester);

            // Fetch paid & unpaid fee entries
            List<Fee> paidFees = feeService.getPaidFeesByStudent(student);
            List<Fee> unpaidFees = feeService.getUnpaidFeesByStudent(student);

            double paidAmount = paidFees.stream().mapToDouble(Fee::getAmount).sum();
            double unpaidAmount = unpaidFees.stream().mapToDouble(Fee::getAmount).sum();
            double totalFee = paidAmount + unpaidAmount;

            // Create map to determine if each AdminAllottedFee is paid
            Map<Long, Boolean> allottedFeePaidStatus = new HashMap<>();
            for (AdminAllottedFee allottedFee : filteredAllottedFees) {
                boolean isPaid = paidFees.stream().anyMatch(fee ->
                        fee.getAmount() == allottedFee.getAmount()
                                && fee.getStudent().getId().equals(student.getId())
                                && fee.getStudent().getSemester().equalsIgnoreCase(allottedFee.getSemester())
                                && fee.getStudent().getDepartment().equalsIgnoreCase(allottedFee.getDepartment())
                );
                allottedFeePaidStatus.put(allottedFee.getId(), isPaid);
            }

            // Pass data to view
            model.addAttribute("student", student);
            model.addAttribute("paidFees", paidFees);
            model.addAttribute("unpaidFees", unpaidFees);
            model.addAttribute("paidAmount", paidAmount);
            model.addAttribute("unpaidAmount", unpaidAmount);
            model.addAttribute("totalFee", totalFee);

            model.addAttribute("semesters", uniqueSemesters); // Dropdown
            model.addAttribute("selectedSemester", selectedSemester); // Selected value in dropdown
            model.addAttribute("filteredAllottedFees", filteredAllottedFees);
            model.addAttribute("allottedFeePaidStatus", allottedFeePaidStatus);

            return "student/fees";
        } else {
            return "redirect:/student/dashboard?error=student_not_found";
        }
    }



    @PostMapping("/student/payAllotted/{id}")
    public String payAllottedFee(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Optional<AdminAllottedFee> optionalFee = feeService.getAllottedFeeById(id);

        if (optionalFee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fee not found.");
            return "redirect:/student/fees";
        }

        AdminAllottedFee allottedFee = optionalFee.get();

        if (allottedFee.getPaymentDate() != null) {
            redirectAttributes.addFlashAttribute("infoMessage", "Fee already paid.");
            return "redirect:/student/fees?filteredSemester=" + allottedFee.getSemester();
        }

        Optional<Student> optionalStudent = studentService.getStudentByLogin(authentication.getName());

        if (optionalStudent.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/student/fees?filteredSemester=" + allottedFee.getSemester();
        }

        Student student = optionalStudent.get();

        Fee fee = new Fee();
        fee.setAmount(allottedFee.getAmount());
        fee.setPaymentDate(LocalDate.now());
        fee.setPaid(true);
        fee.setStudent(student);
        feeService.saveStudentFee(fee);



        redirectAttributes.addFlashAttribute("successMessage", "Fee paid successfully.");
        return "redirect:/student/fees?filteredSemester=" + allottedFee.getSemester();
    }


}
