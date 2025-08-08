package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // Assign a course to a staff member
    public void assignCourseToStaff(Long courseId, Staff staff) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            course.setStaff(staff); // Set staff on the course
            courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with id: " + courseId);
        }
    }


    // Get all courses handled by a staff
    public List<Course> getCoursesHandledByStaff(Staff staff) {
        return courseRepository.findByStaff(staff);
    }

    // Get all available courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByStaff(Staff staff) {
        return courseRepository.findByStaff(staff);
    }


}
