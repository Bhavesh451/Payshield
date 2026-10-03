package com.payshield.controller;

import com.payshield.entity.Transaction;
import com.payshield.entity.User;
import com.payshield.service.AdminService;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return adminService.getAllTransactions();
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("totalUsers", adminService.getTotalUsers());
        stats.put(
                "totalTransactions",
                adminService.getTotalTransactions()
        );

        return stats;
    }
}