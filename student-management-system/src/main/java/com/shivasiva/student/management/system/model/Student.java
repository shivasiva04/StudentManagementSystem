package com.shivasiva.student.management.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
public class Student {

    @Id
    private String id;

    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    private String department;

    private boolean registered;

    // ✅ Many-to-Many Relationship with Course
    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses; // holds list of enrolled courses
}
