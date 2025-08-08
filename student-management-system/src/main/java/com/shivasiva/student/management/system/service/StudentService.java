package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    private final Map<String, Student> studentMap = new HashMap<>();

    public void addStudent(Student student) {
        studentMap.put(student.getId(), student);
    }

    public void updateStudent(String id, Student updated) {
        studentMap.put(id, updated);
    }

    public void deleteStudent(String id) {
        studentMap.remove(id);
    }

    public Student getStudent(String id) {
        return studentMap.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentMap.values());
    }

    public List<Student> findStudentsByStaffCourses(Staff staff) {
        return studentRepository.findStudentsByCourses_Staff(staff);
    }

    public List<Student> getStudentsByDeptAndSem(String department, String semester) {
        if (department != null && semester != null) {
            return studentRepository.findByDepartmentAndSemester(department, semester);
        } else {
            return studentRepository.findAll();
        }
    }


    public List<String> getAllDepartments() {
        return List.of(
                "Computer Science",
                "Information Technology",
                "Electronics and Communication",
                "Mechanical Engineering",
                "Civil Engineering",
                "Biomedical Engineering"
        );
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> getStudentByLogin(String loginValue) {
        Optional<Student> student = studentRepository.findByEmail(loginValue);
        if (student.isEmpty()) {
            student = studentRepository.findByName(loginValue); // fallback
        }
        return student;
    }


}
