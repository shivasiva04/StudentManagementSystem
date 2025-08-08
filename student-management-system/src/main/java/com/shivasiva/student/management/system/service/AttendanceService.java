package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.Attendance;
import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void markAttendance(Course course, List<Long> presentStudentIds) {
        LocalDate today = LocalDate.now();

        for (Student student : course.getStudents()) {
            boolean isPresent = presentStudentIds.contains(student.getId());

            Attendance attendance = new Attendance();
            attendance.setCourse(course);
            attendance.setStudent(student);
            attendance.setDate(today);
            attendance.setPresent(isPresent);

            attendanceRepository.save(attendance);
        }
    }

    public void saveAttendance(Course course, Student student, LocalDate date, boolean isPresent) {
        Attendance attendance = new Attendance();
        attendance.setCourse(course);
        attendance.setStudent(student);
        attendance.setDate(date);
        attendance.setPresent(isPresent);

        attendanceRepository.save(attendance);
    }
    public List<Attendance> getAttendanceByCourseAndDate(Course course, LocalDate date) {
        return attendanceRepository.findByCourseAndDate(course, date);
    }

    public double getAttendancePercentageByCourse(Course course) {
        List<Student> enrolledStudents = course.getStudents();
        if (enrolledStudents == null || enrolledStudents.isEmpty()) return 0.0;

        double totalPercentage = 0.0;

        for (Student student : enrolledStudents) {
            totalPercentage += getAttendancePercentageByCourse(student, course);
        }

        return totalPercentage / enrolledStudents.size();
    }


    public double getAttendancePercentageByCourse(Student student, Course course) {
        List<Attendance> attendances = attendanceRepository.findByStudentAndCourse(student, course);

        if (attendances.isEmpty()) return 0.0;

        long totalDays = attendances.size();
        long presentDays = attendances.stream()
                .filter(Attendance::isPresent)
                .count();

        return (double) presentDays / totalDays * 100;
    }

    public Map<String, Double> getDailyAttendanceForMonth(Student student, Month month, int year) {
        List<Attendance> attendanceList = attendanceRepository.findByStudent(student);

        Map<LocalDate, List<Attendance>> groupedByDate = attendanceList.stream()
                .filter(a -> a.getDate().getMonth() == month && a.getDate().getYear() == year)
                .collect(Collectors.groupingBy(Attendance::getDate));

        Map<String, Double> dailyPercentMap = new LinkedHashMap<>();

        int daysInMonth = month.length(LocalDate.of(year, 1, 1).isLeapYear());
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = LocalDate.of(year, month, i);
            List<Attendance> daily = groupedByDate.getOrDefault(date, List.of());

            if (!daily.isEmpty()) {
                long presentCount = daily.stream().filter(a -> a.isPresent()).count();
                double percentage = (presentCount * 100.0) / daily.size();
                dailyPercentMap.put(String.format("%02d %s", i, month.name().substring(0, 3)), percentage);
            } else {
                dailyPercentMap.put(String.format("%02d %s", i, month.name().substring(0, 3)), 0.0);
            }
        }

        return dailyPercentMap;
    }




}
