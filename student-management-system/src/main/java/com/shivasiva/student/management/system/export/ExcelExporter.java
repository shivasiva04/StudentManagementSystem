package com.shivasiva.student.management.system.export;
import com.shivasiva.student.management.system.model.Attendance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelExporter {
    public static void exportAttendance(List<Attendance> attendanceList, OutputStream out) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student Name");
        header.createCell(1).setCellValue("Status");
        header.createCell(2).setCellValue("Date");

        int rowIdx = 1;
        for (Attendance attendance : attendanceList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(attendance.getStudent().getName());
            row.createCell(1).setCellValue(attendance.isPresent() ? "Present" : "Absent");
            row.createCell(2).setCellValue(attendance.getDate().toString());
        }

        workbook.write(out);
        workbook.close();
    }
}
