package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/add")
    public String showAddStaffPage(Model model) {
        model.addAttribute("staff", new Staff());

        List<Staff> allStaff = staffService.findAll();
        model.addAttribute("staffList", allStaff);

        Set<String> uniqueDepartments = allStaff.stream()
                .map(Staff::getDepartment)
                .collect(Collectors.toSet());
        model.addAttribute("departments", uniqueDepartments);

        return "admin/staff-add";
    }

    @PostMapping("/save")
    public String saveStaff(@ModelAttribute Staff staff) {
        staffService.save(staff);
        return "redirect:/admin/staff/add";
    }


}
