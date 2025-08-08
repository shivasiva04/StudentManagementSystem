package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.service.StaffService;
import com.shivasiva.student.management.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final UserService userService;
    @Autowired
    private StaffService staffService;


    public StaffDashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Model model, Authentication authentication) {
        String loginInput = authentication.getName(); // Can be email or username

        // Try email first
        Optional<User> optionalUser = userService.getUserByEmail(loginInput);
        if (optionalUser.isEmpty()) {
            optionalUser = userService.getUserByUsername(loginInput);
        }

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found: " + loginInput);
            return "error/staff-not-found";
        }

        User user = optionalUser.get();

        if (!"STAFF".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("error", "Not a staff account: " + loginInput);
            return "error/staff-not-found";
        }

        Optional<Staff> optionalStaff = staffService.findByEmail(user.getEmail());
        if (optionalStaff.isEmpty()) {
            model.addAttribute("error", "Staff account not found for: " + user.getEmail());
            return "error/staff-not-found";
        }

        Staff staff = optionalStaff.get();
        model.addAttribute("staff", staff);
        model.addAttribute("courses", staff.getCourses());

        return "dashboard/staff-dashboard";
    }


}
