package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.service.CourseService;
import com.shivasiva.student.management.system.service.StaffService;
import com.shivasiva.student.management.system.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
//import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff")
public class StaffCourseController {

    private final StaffService staffService;
    private final CourseService courseService;
    private final StudentService studentService;

    public StaffCourseController(StaffService staffService, CourseService courseService, StudentService studentService) {
        this.staffService = staffService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    //  Show available + assigned courses
    @GetMapping("/courses")
    public String showAvailableCourses(@RequestParam(value = "semester", required = false) String semester,
                                       Model model, Authentication authentication) {
        String loginValue = authentication.getName();
        Staff staff = staffService.findByEmailOrName(loginValue)
                .orElseThrow(() -> new RuntimeException("Staff not found with email or name: " + loginValue));

        // Only get courses that belong to the staff's department
        List<Course> allCourses = courseService.getAllCourses()
                .stream()
                .filter(course -> course.getDepartment().equalsIgnoreCase(staff.getDepartment()))
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(Collectors.toList());


        List<Course> assignedCourses = staff.getCourses();
        Set<Long> assignedCourseIds = assignedCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toSet());

        List<Course> unassignedCourses = allCourses.stream()
                .filter(course -> course.getStaff() == null || !assignedCourseIds.contains(course.getId()))
                .collect(Collectors.toList());

        if (semester != null && !semester.isEmpty()) {
            assignedCourses = assignedCourses.stream()
                    .filter(course -> semester.equals(course.getSemester()))
                    .collect(Collectors.toList());

            unassignedCourses = unassignedCourses.stream()
                    .filter(course -> semester.equals(course.getSemester()))
                    .collect(Collectors.toList());
        }

        Set<String> semesters = allCourses.stream()
                .map(Course::getSemester)
                .collect(Collectors.toCollection(TreeSet::new));

        model.addAttribute("staff", staff);
        model.addAttribute("courses", unassignedCourses);
        model.addAttribute("assignedCourses", assignedCourses);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("semesters", semesters);

        return "staff/manage-courses";
    }




    //  Assign course to logged-in staff
    @PostMapping("/assignCourse")
    public String assignCourse(@RequestParam("courseId") Long courseId, Authentication authentication) {
        String loginValue = authentication.getName();
        Staff staff = staffService.findByEmailOrName(loginValue)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        courseService.assignCourseToStaff(courseId, staff);
        return "redirect:/staff/courses";
    }

    //  View students assigned to a course (authorized)
    @GetMapping("/students")
    public String viewStudents(@RequestParam("courseId") Long courseId,
                               Model model,
                               Authentication authentication) {
        String loginValue = authentication.getName();
        Staff staff = staffService.findByEmailOrName(loginValue)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getStaff().getId().equals(staff.getId())) {
            throw new RuntimeException("You are not authorized to view this course's students.");
        }

        List<Student> students = course.getStudents()
                .stream()
                .sorted((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()))
                .toList();

        model.addAttribute("students", students);
        model.addAttribute("course", course);
        return "staff/view-students";
    }

}
