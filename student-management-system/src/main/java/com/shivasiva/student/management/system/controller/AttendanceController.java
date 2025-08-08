package com.shivasiva.student.management.system.controller;

import com.shivasiva.student.management.system.export.ExcelExporter;
import com.shivasiva.student.management.system.export.PdfExporter;
import com.shivasiva.student.management.system.model.Attendance;
import com.shivasiva.student.management.system.model.Course;
import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.model.Student;
import com.shivasiva.student.management.system.repository.AttendanceRepository;
import com.shivasiva.student.management.system.repository.CourseRepository;
import com.shivasiva.student.management.system.service.AttendanceService;
import com.shivasiva.student.management.system.service.CourseService;
import com.shivasiva.student.management.system.service.StaffService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.DocumentException;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/staff")
public class AttendanceController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/attendance")
    public String showCourseSelection(Model model, Authentication authentication) {
        String loginValue = authentication.getName();
        Staff staff = staffService.findByEmailOrName(loginValue).orElseThrow();
        model.addAttribute("courses", staff.getCourses());
        return "staff/attendance-course-select";
    }

    @GetMapping("/attendance/mark")
    public String showAttendancePage(@RequestParam("courseId") Long courseId,
                                     @RequestParam(value = "date", required = false) String dateParam,
                                     @RequestParam(value = "justSubmitted", required = false) String justSubmittedParam,
                                     @RequestParam(value = "alreadySubmitted", required = false) String alreadySubmittedParam,
                                     @RequestParam(value = "error", required = false) String error,
                                     Model model) {

        Course course = courseService.getCourseById(courseId).orElseThrow();
        LocalDate attendanceDate = (dateParam != null) ? LocalDate.parse(dateParam) : LocalDate.now();

        boolean attendanceExists = !attendanceService.getAttendanceByCourseAndDate(course, attendanceDate).isEmpty();

        boolean justSubmittedFlag = "true".equalsIgnoreCase(justSubmittedParam);
        boolean alreadySubmittedFlag = attendanceExists && ("true".equalsIgnoreCase(alreadySubmittedParam) || (justSubmittedParam == null && dateParam != null));

        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());
        model.addAttribute("attendanceDate", attendanceDate);
        model.addAttribute("attendanceSubmitted", attendanceExists);
        model.addAttribute("justSubmitted", justSubmittedFlag);
        model.addAttribute("alreadySubmitted", alreadySubmittedFlag);
        model.addAttribute("error", error);

        return "staff/mark-attendance";
    }

    @PostMapping("/attendance/mark")
    public String submitAttendance(@RequestParam Long courseId,
                                   @RequestParam(value = "date", required = false) String date,
                                   @RequestParam MultiValueMap<String, String> formParams) {

        if (date == null || date.trim().isEmpty()) {
            return "redirect:/staff/attendance/mark?courseId=" + courseId + "&error=missing_date";
        }

        LocalDate attendanceDate;
        try {
            attendanceDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return "redirect:/staff/attendance/mark?courseId=" + courseId + "&error=invalid_date";
        }

        Course course = courseService.getCourseById(courseId).orElseThrow();

        boolean alreadyExists = !attendanceService.getAttendanceByCourseAndDate(course, attendanceDate).isEmpty();

        if (alreadyExists) {
            return "redirect:/staff/attendance/mark?courseId=" + courseId + "&date=" + date + "&alreadySubmitted=true";
        }

        for (Student student : course.getStudents()) {
            String paramName = "attendance_" + student.getId();
            String status = formParams.getFirst(paramName);

            if (status != null) {
                boolean isPresent = "present".equals(status);
                attendanceService.saveAttendance(course, student, attendanceDate, isPresent);
            }
        }

        return "redirect:/staff/attendance/mark?courseId=" + courseId + "&date=" + date + "&justSubmitted=true";
    }

    @GetMapping("/attendance/view")
    public String viewAttendance(@RequestParam Long courseId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Model model) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        List<Attendance> records;

        if (date != null) {
            records = attendanceRepository.findByCourseAndDate(course, date);
            model.addAttribute("filterDate", date);
        } else {
            records = attendanceRepository.findByCourse(course);
        }

        model.addAttribute("attendanceRecords", records);
        model.addAttribute("course", course);
        return "staff/view-attendance";
    }

    @GetMapping("/attendance/export/excel")
    public void exportExcel(@RequestParam("courseId") Long courseId,
                            @RequestParam(value = "date", required = false) String date,
                            HttpServletResponse response) throws IOException {

        LocalDate attendanceDate;
        try {
            attendanceDate = (date == null || date.trim().isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
        } catch (Exception e) {
            // Return error if date is invalid format
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format");
            return;
        }

        Course course = courseService.getCourseById(courseId).orElseThrow();
        List<Attendance> attendanceList = attendanceService.getAttendanceByCourseAndDate(course, attendanceDate);

        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=attendance_" + course.getName() + "_" + attendanceDate + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelExporter.exportAttendance(attendanceList, response.getOutputStream());
    }

    @GetMapping("/attendance/export/pdf")
    public void exportPdf(@RequestParam("courseId") Long courseId,
                          @RequestParam(value = "date", required = false) String date,
                          HttpServletResponse response) throws IOException, DocumentException {

        LocalDate attendanceDate;
        try {
            attendanceDate = (date == null || date.trim().isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format");
            return;
        }

        Course course = courseService.getCourseById(courseId).orElseThrow();
        List<Attendance> attendanceList = attendanceService.getAttendanceByCourseAndDate(course, attendanceDate);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_" + course.getName() + "_" + attendanceDate + ".pdf");

        PdfExporter.exportAttendance(attendanceList, response.getOutputStream());
    }






}
