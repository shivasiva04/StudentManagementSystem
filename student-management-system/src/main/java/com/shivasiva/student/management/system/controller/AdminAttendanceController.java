package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Attendance;
import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.repository.AttendanceRepository;
import com.shivasiva.student.management.system.repository.CourseRepository;
import com.shivasiva.student.management.system.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/attendance")
public class AdminAttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping
    public String viewAttendance(@RequestParam(required = false) Long courseId,
                                 @RequestParam(required = false) Long staffId,
                                 @RequestParam(required = false) String semester,
                                 @RequestParam(required = false) String department,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Model model) {

        List<Course> allCourses = courseRepository.findAll();
        List<Staff> allStaff = staffRepository.findAll();

        // Extract all distinct semesters and departments from courses
        List<String> allSemesters = allCourses.stream().map(Course::getSemester).distinct().toList();
        List<String> allDepartments = allCourses.stream().map(Course::getDepartment).distinct().toList();

        List<Attendance> attendanceList;

        // Initial base list
        if (courseId != null && date != null) {
            Course course = courseRepository.findById(courseId).orElse(null);
            attendanceList = attendanceRepository.findByCourseAndDate(course, date);
        } else if (courseId != null) {
            Course course = courseRepository.findById(courseId).orElse(null);
            attendanceList = attendanceRepository.findByCourse(course);
        } else {
            attendanceList = attendanceRepository.findAll();
        }

        // Apply staff filter
        if (staffId != null) {
            Staff staff = staffRepository.findById(staffId).orElse(null);
            attendanceList = attendanceList.stream()
                    .filter(a -> a.getCourse().getStaff().equals(staff))
                    .toList();
        }

        // Apply semester filter
        if (semester != null && !semester.isBlank()) {
            attendanceList = attendanceList.stream()
                    .filter(a -> semester.equalsIgnoreCase(a.getCourse().getSemester()))
                    .toList();
        }

        // Apply department filter
        if (department != null && !department.isBlank()) {
            attendanceList = attendanceList.stream()
                    .filter(a -> department.equalsIgnoreCase(a.getCourse().getDepartment()))
                    .toList();
        }

        // Pass data to view
        model.addAttribute("attendanceList", attendanceList);
        model.addAttribute("allCourses", allCourses);
        model.addAttribute("allStaff", allStaff);
        model.addAttribute("allSemesters", allSemesters);
        model.addAttribute("allDepartments", allDepartments);

        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedStaffId", staffId);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedDate", date);

        return "admin/view-attendance";
    }

}
