package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public String showCourses(@RequestParam(value = "semester", required = false) String semester,
                              @RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "success", required = false) String success,
                              Model model) {

        List<Course> courses;
        if (semester != null && !semester.isEmpty()) {
            courses = courseRepository.findBySemester(semester);
        } else {
            courses = courseRepository.findAll();
        }

        model.addAttribute("courses", courses);
        model.addAttribute("newCourse", new Course());
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("error", error);
        model.addAttribute("success", success); // Added success message

        return "admin/manage-courses";
    }

    @PostMapping("/add")
    public String addCourse(@ModelAttribute("newCourse") Course course) {

        // Validate input
        if (course.getName() == null || course.getSemester() == null || course.getDepartment() == null ||
                course.getName().isEmpty() || course.getSemester().isEmpty() || course.getDepartment().isEmpty()) {

            return "redirect:/admin/courses?error=Please+fill+all+fields";
        }

        // Check for duplicates
        List<Course> existingCourses = courseRepository.findByNameAndDepartmentAndSemester(
                course.getName(), course.getDepartment(), course.getSemester());

        if (!existingCourses.isEmpty()) {
            return "redirect:/admin/courses?error=Course+already+exists+for+this+department+and+semester";
        }

        // Save course
        courseRepository.save(course);

        // Redirect with success message to avoid form resubmission
        return "redirect:/admin/courses?success=Course+added+successfully";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/courses?success=Course+deleted";
    }
}
