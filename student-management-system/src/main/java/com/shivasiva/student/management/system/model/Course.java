package com.shivasiva.student.management.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String description;
}
