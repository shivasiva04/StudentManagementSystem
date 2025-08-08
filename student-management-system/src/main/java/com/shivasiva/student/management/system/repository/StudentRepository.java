package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByName(String name);

    List<Student> findStudentsByCourses_Staff(Staff staff);

    List<Student> findByDepartmentAndSemester(String department, String semester);

}
