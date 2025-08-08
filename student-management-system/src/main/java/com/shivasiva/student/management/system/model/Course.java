package com.shivasiva.student.management.system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data  // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Lombok: Generates no-args constructor
@AllArgsConstructor // Lombok: Generates all-args constructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String semester;
    private String department;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();


    private String description;
}
