package com.shivasiva.student.management.system.controller;

//import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.model.PendingRegistration;
import com.shivasiva.student.management.system.service.StaffService;
import com.shivasiva.student.management.system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import java.util.Optional;
//import java.util.Random;

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
        return "staff-signup";
    }

    @PostMapping("/signup")
    public String staffSignup(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              Model model,
                              HttpSession session) {

        email = email.trim();

        // Validate only here: user doesn't already exist
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "You have already signed up.");
            return "staff-signup";
        }

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists.");
            return "staff-signup";
        }

        // Attempt to register (this handles staff existence + OTP + saving)
        User user = new User(username, password, "STAFF", email, false);
        boolean registered = userService.registerUser(user);

        if (!registered) {
            model.addAttribute("error", "You are not pre-registered as staff.");
            return "staff-signup";
        }

        session.setAttribute("pendingRegistration", new PendingRegistration(username, password, email, "STAFF", null));
        model.addAttribute("username", username);
        return "otp";
    }




    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp,
                            HttpSession session,
                            Model model) {
        PendingRegistration pending = (PendingRegistration) session.getAttribute("pendingRegistration");

        if (pending != null && pending.getOtp().equals(otp)) {
            User user = new User(
                    pending.getUsername(),
                    pending.getPassword(),
                    pending.getRole(),
                    pending.getEmail(),
                    true
            );

            boolean registered = userService.registerUser(user);

            if (registered) {
                staffService.markAsRegistered(pending.getEmail());
                session.removeAttribute("pendingRegistration");
                return "redirect:/login";
            } else {
                model.addAttribute("error", "Registration failed.");
                return "staff-signup";
            }
        } else {
            model.addAttribute("error", "Invalid OTP.");
            return "otp";
        }
    }
}
