package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findBySemester(String semester);
    List<Course> findByDepartment(String department);
    List<Course> findByNameAndDepartmentAndSemester(String name, String department, String semester);

    Course findByCode(String code);
}
