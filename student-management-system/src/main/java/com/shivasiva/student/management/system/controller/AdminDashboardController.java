package com.shivasiva.student.management.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "dashboard/admin-dashboard";
    }


}
