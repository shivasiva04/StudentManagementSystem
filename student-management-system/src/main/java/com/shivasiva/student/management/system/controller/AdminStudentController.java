    package com.shivasiva.student.management.system.controller;

    import com.shivasiva.student.management.system.model.Course;
    import com.shivasiva.student.management.system.model.Student;
    import com.shivasiva.student.management.system.repository.CourseRepository;
    import com.shivasiva.student.management.system.repository.StudentRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import jakarta.servlet.http.HttpServletRequest;  // import this for CSRF token
    import org.springframework.security.web.csrf.CsrfToken;

    import java.util.List;

    @Controller
    @RequestMapping("/admin/students")
    public class AdminStudentController {

        @Autowired
        private StudentRepository studentRepository;

        @Autowired
        private CourseRepository courseRepository;

        @GetMapping
        public String listStudents(Model model, HttpServletRequest request) {
            model.addAttribute("students", studentRepository.findAll());

            // Add CSRF token to model for Thymeleaf
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

            model.addAttribute("_csrf", csrfToken);

            return "dashboard/admin-students";
        }

        @PostMapping("/add")
        public String addStudent(@RequestParam(required = false) String id,
                                 @RequestParam String name,
                                 @RequestParam String department,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 Model model) {
            if (studentRepository.existsByEmail(email)) {
                model.addAttribute("error", "Email already exists!");
                model.addAttribute("students", studentRepository.findAll());
                return "dashboard/admin-students";
            }

            // Skip course lookup — safe if not assigning yet
            List<Course> courses = List.of();

            Student student = new Student();
            if (id != null && !id.isBlank()) {
                student.setId(id);
            }
            student.setName(name);
            student.setEmail(email);
            student.setPhone(phone);
            student.setDepartment(department);
            student.setCourses(courses);

            studentRepository.save(student);
            return "redirect:/admin/students";
        }


        @GetMapping("/delete/{id}")
        public String deleteStudent(@PathVariable String id) {
            studentRepository.deleteById(id);
            return "redirect:/admin/students";
        }
    }
