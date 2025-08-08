package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.Attendance;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByCourseAndDate(Course course, LocalDate date);
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByStudentAndCourse(Student student, Course course);
    List<Attendance> findByCourse_IdAndDateAndCourse_Staff_Id(Long courseId, LocalDate date, Long staffId);


}
