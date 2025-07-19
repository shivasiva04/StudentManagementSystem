package com.shivasiva.student.management.system.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
    private String email;
    private boolean verified;

    public User(String username, String password, String role, String email, boolean verified) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.verified = verified;
    }
}
