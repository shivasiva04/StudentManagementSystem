package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.Student;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {
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
}
