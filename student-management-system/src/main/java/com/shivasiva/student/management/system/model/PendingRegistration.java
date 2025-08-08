package com.shivasiva.student.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingRegistration {
    private String username;
    private String password;
    private String email;
    private String role;
    private String otp;
}

