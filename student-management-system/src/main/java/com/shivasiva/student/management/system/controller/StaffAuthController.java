package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.service.StaffService;
import com.shivasiva.student.management.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/staff")
public class StaffAuthController {

    private final StaffService staffService;
    private final UserService userService;

    @Autowired
    public StaffAuthController(StaffService staffService, UserService userService) {
        this.staffService = staffService;
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showStaffSignupForm() {
        return "staff-signup";  // Thymeleaf template
    }

    @PostMapping("/signup")
    public String staffSignup(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              Model model) {

        Optional<Staff> optionalStaff = staffService.findByEmail(email.trim());


        if (optionalStaff.isEmpty()) {
            model.addAttribute("error", "You are not pre-registered as staff.");
            return "staff-signup";
        }

        Staff staff = optionalStaff.get();

        if (staff.isRegistered()) {
            model.addAttribute("error", "You have already signed up.");
            return "staff-signup";
        }

        // No phone check needed — staff can use any password
        boolean created = userService.registerUser(
                new User(username, password, "STAFF", email, false)
        );

        if (created) {
            staff.setRegistered(true);
            staffService.save(staff);
            model.addAttribute("username", username);
            return "otp";  // redirect to OTP verification
        }

        model.addAttribute("error", "Username already exists.");
        return "staff-signup";
    }
}
