package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Admin;
import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.repository.AdminRepository;
import com.shivasiva.student.management.system.repository.CourseRepository;
import com.shivasiva.student.management.system.repository.StaffRepository;
import com.shivasiva.student.management.system.repository.StudentRepository;
import com.shivasiva.student.management.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.apache.catalina.manager.JspHelper.formatNumber;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalStaff = staffRepository.count();

        model.addAttribute("totalStudents", formatNumber(totalStudents));
        model.addAttribute("totalCourses", formatNumber(totalCourses));
        model.addAttribute("totalStaff", formatNumber(totalStaff));
        model.addAttribute("earnings", "₹0");

        return "dashboard/admin-dashboard";
    }

    @GetMapping("/settings")
    public String showAdminSettings(Model model, Authentication authentication) {
        String loginInput = authentication.getName(); // username or email

        // Try to fetch User by email first, then username
        Optional<User> optionalUser = userService.getUserByEmail(loginInput);
        if (optionalUser.isEmpty()) {
            optionalUser = userService.getUserByUsername(loginInput);
        }

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found: " + loginInput);
            return "error/admin-not-found";
        }

        User user = optionalUser.get();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("error", "Not an admin account: " + loginInput);
            return "error/admin-not-found";
        }

        Admin admin = adminRepository.findByEmail(user.getEmail());
        if (admin == null) {
            model.addAttribute("error", "Admin account not found for: " + user.getEmail());
            return "error/admin-not-found";
        }

        model.addAttribute("userName", admin.getName());
        model.addAttribute("userEmail", admin.getEmail());
        model.addAttribute("createdAt", admin.getCreatedAt());

        return "dashboard/admin-settings";
    }
}
