//package com.shivasiva.student.management.system.config;
//
//import com.shivasiva.student.management.system.model.Course;
//import com.shivasiva.student.management.system.repository.CourseRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class CourseDataConfig {
//
//    @Bean
//    public CommandLineRunner seedCourses(CourseRepository courseRepository) {
//        return args -> {
//            if (courseRepository.count() == 0) {
//                courseRepository.save(new Course(null, "Data Structures", "CS101", "Semester 1", "Computer Science", "Core CS course"));
//                courseRepository.save(new Course(null, "IT Basics", "IT101", "Semester 1", "Information Technology", "Intro IT course"));
//                courseRepository.save(new Course(null, "Digital Circuits", "ECE101", "Semester 1", "Electronics and Communication", "ECE core"));
//                courseRepository.save(new Course(null, "Thermodynamics", "ME101", "Semester 1", "Mechanical Engineering", "ME core"));
//                courseRepository.save(new Course(null, "Surveying", "CE101", "Semester 1", "Civil Engineering", "CE core"));
//                courseRepository.save(new Course(null, "Human Biology", "BM101", "Semester 1", "Biomedical Engineering", "Bio core"));
//            }
//        };
//    }
//}
