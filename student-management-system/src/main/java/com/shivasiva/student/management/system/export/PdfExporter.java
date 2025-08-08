package com.shivasiva.student.management.system.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.shivasiva.student.management.system.model.Attendance;

import java.io.OutputStream;
import java.util.List;

public class PdfExporter {
    public static void exportAttendance(List<Attendance> attendanceList, OutputStream out) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph title = new Paragraph("Attendance Report", font);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        table.addCell("Student Name");
        table.addCell("Status");
        table.addCell("Date");

        for (Attendance attendance : attendanceList) {
            table.addCell(attendance.getStudent().getName());
            table.addCell(attendance.isPresent() ? "Present" : "Absent");
            table.addCell(attendance.getDate().toString());
        }

        document.add(table);
        document.close();
    }
}
