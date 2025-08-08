package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Admin;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.repository.AdminRepository;
import com.shivasiva.student.management.system.repository.StudentRepository;
import com.shivasiva.student.management.system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    //  Login page
    @GetMapping({"/", "/login"})
    public String loginForm() {
        return "login";
    }

    //  Signup page
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    //  Signup logic (Safe Optional handling + student pre-check)
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String role,
                         @RequestParam String email,
                         Model model) {

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists.");
            return "signup";
        }

        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists.");
            return "signup";
        }

        if (role.equalsIgnoreCase("STUDENT")) {
            Optional<Student> optionalStudent = studentRepository.findByEmail(email);
            return optionalStudent.map(student -> {
                if (student.isRegistered()) {
                    model.addAttribute("error", "You have already signed up.");
                    return "signup";
                }
                if (!student.getPhone().equals(password)) {
                    model.addAttribute("error", "Invalid password (use your phone number).");
                    return "signup";
                }

                boolean created = userService.registerUser(new User(username, password, role, email, false));
                if (created) {
                    student.setRegistered(true);
                    studentRepository.save(student);
                    model.addAttribute("username", username);
                    return "otp";
                }

                model.addAttribute("error", "Could not create account.");
                return "signup";
            }).orElseGet(() -> {
                model.addAttribute("error", "You are not pre-registered.");
                return "signup";
            });
        }

        if (role.equalsIgnoreCase("STAFF")) {
            boolean created = userService.registerUser(new User(username, password, role, email, false));
            if (created) {
                model.addAttribute("username", username);
                return "otp";
            }

            model.addAttribute("error", "You are not pre-registered as staff.");
            return "signup";
        }

        //  Default (e.g., ADMIN) — updated logic here
        boolean created = userService.registerUser(new User(username, password, role, email, false));
        if (created) {
            if (role.equalsIgnoreCase("ADMIN")) {
                Admin admin = new Admin();
                admin.setName(username);
                admin.setEmail(email);
                admin.setCreatedAt(LocalDateTime.now()); // optional, if not set automatically
                admin.setPassword(password); // Not mandatory unless needed in admin table
                adminRepository.save(admin);  // Save to admin table
            }


            model.addAttribute("username", username);
            return "otp";
        }

        model.addAttribute("error", "Username already exists.");
        return "signup";
    }


    //  OTP verification logic
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String username,
                            @RequestParam String otp,
                            Model model) {
        boolean verified = userService.verifyOtp(username, otp);
        if (verified) return "redirect:/login";

        model.addAttribute("error", "Invalid OTP");
        model.addAttribute("username", username);
        return "otp";
    }

    // Role-based redirection after login
    @GetMapping("/redirect")
    public String redirect(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user.getRole().equalsIgnoreCase("ADMIN") ?
                    "redirect:/admin/dashboard" :
                    "redirect:/student/dashboard";
        }
        return "redirect:/";
    }

    //  Logout logic
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
