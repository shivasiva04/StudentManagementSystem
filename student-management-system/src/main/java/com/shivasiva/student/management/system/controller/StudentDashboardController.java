//package com.shivasiva.student.management.system.controller;
//
//import com.shivasiva.student.management.system.model.Course;
//import com.shivasiva.student.management.system.model.Student;
//import com.shivasiva.student.management.system.repository.CourseRepository;
//import com.shivasiva.student.management.system.repository.StudentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//public class StudentDashboardController {
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Autowired
//    private CourseRepository courseRepository;
//
//    @GetMapping("/student/dashboard")
//    public String studentDashboard(Authentication authentication, Model model) {
//        String loginValue = authentication.getName(); // email or username
//        System.out.println("🔐 Logged in value: " + loginValue);
//
//        Optional<Student> optionalStudent = studentRepository.findByEmail(loginValue);
//
//        if (optionalStudent.isEmpty()) {
//            optionalStudent = studentRepository.findByName(loginValue);
//        }
//
//        if (optionalStudent.isPresent()) {
//            Student student = optionalStudent.get();
//            model.addAttribute("student", student);
//            model.addAttribute("studentName", student.getName());
//            model.addAttribute("enrolledCourses", student.getCourses());
//
//            // 🔥 Filter courses by student's department only
//            List<Course> departmentCourses = courseRepository.findByDepartment(student.getDepartment());
//            model.addAttribute("courses", departmentCourses);
//
//        } else {
//            System.out.println("⚠️ Student not found for: " + loginValue);
//            model.addAttribute("student", new Student());
//            model.addAttribute("studentName", "Guest");
//            model.addAttribute("enrolledCourses", List.of());
//            model.addAttribute("courses", List.of());
//        }
//
//        return "dashboard/student-dashboard";
//    }
//}









//package com.shivasiva.student.management.system.controller;
//
//import com.shivasiva.student.management.system.model.Course;
//import com.shivasiva.student.management.system.model.Student;
//import com.shivasiva.student.management.system.repository.CourseRepository;
//import com.shivasiva.student.management.system.repository.StudentRepository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.LinkedHashSet; // ✅ Correct import
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Controller
//public class StudentDashboardController {
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Autowired
//    private CourseRepository courseRepository;
//
//    @GetMapping("/student/dashboard")
//    public String studentDashboard(Authentication authentication, Model model) {
//        String loginValue = authentication.getName(); // email or name (username)
//        System.out.println("🔐 Logged in value: " + loginValue);
//
//        Optional<Student> optionalStudent = studentRepository.findByEmail(loginValue);
//        if (optionalStudent.isEmpty()) {
//            optionalStudent = studentRepository.findByName(loginValue);
//        }
//
//        if (optionalStudent.isPresent()) {
//            Student student = optionalStudent.get();
//
//            model.addAttribute("student", student);
//            model.addAttribute("studentName", student.getName());
//            model.addAttribute("enrolledCourses", student.getCourses());
//
//            // 🔥 Filter only department-specific courses for the student
//            List<Course> departmentCourses = courseRepository.findByDepartment(student.getDepartment());
//
//            Set<String> uniqueSemesters = departmentCourses.stream()
//                    .map(Course::getSemester)
//                    .collect(Collectors.toCollection(LinkedHashSet::new));
//
//            model.addAttribute("courses", departmentCourses);
//            model.addAttribute("uniqueSemesters", uniqueSemesters);
//
//        } else {
//            System.out.println("⚠️ Student not found for: " + loginValue);
//            model.addAttribute("student", new Student());
//            model.addAttribute("studentName", "Guest");
//            model.addAttribute("enrolledCourses", List.of());
//            model.addAttribute("courses", List.of());
//        }
//
//        return "dashboard/student-dashboard";
//    }
//
//}











package com.shivasiva.student.management.system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.repository.CourseRepository;
import com.shivasiva.student.management.system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentDashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication authentication, Model model) throws JsonProcessingException {
        String loginValue = authentication.getName();
        System.out.println("🔐 Logged in value: " + loginValue);

        Optional<Student> optionalStudent = studentRepository.findByEmail(loginValue);
        if (optionalStudent.isEmpty()) {
            optionalStudent = studentRepository.findByName(loginValue);
        }

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            List<Course> departmentCourses = courseRepository.findByDepartment(student.getDepartment());
            Set<String> uniqueSemesters = departmentCourses.stream()
                    .map(Course::getSemester)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            List<Course> enrolledCourses = student.getCourses();

            model.addAttribute("student", student);
            model.addAttribute("studentName", student.getName());
            model.addAttribute("courses", departmentCourses);
            model.addAttribute("uniqueSemesters", uniqueSemesters);
            model.addAttribute("enrolledCourses", enrolledCourses);

            // JSON data for JS filtering
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
}









