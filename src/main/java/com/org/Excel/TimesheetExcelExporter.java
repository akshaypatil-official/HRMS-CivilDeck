package com.org.Excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import com.org.Entity.Timesheet;

import org.apache.poi.ss.util.CellRangeAddress;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TimesheetExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Timesheet> listTimesheets;
    private String loggedInUserName;

    public TimesheetExcelExporter(List<Timesheet> listTimesheets,String loggedInUserName) {
        this.listTimesheets = listTimesheets;
        this.loggedInUserName = loggedInUserName;
        workbook = new XSSFWorkbook();
    }

    public void export(HttpServletResponse response) throws IOException {
        sheet = workbook.createSheet("Attendance Report");

        // 1. Create Styles
        CellStyle titleStyle = createStyle(true, 14, true);
        CellStyle headerStyle = createStyle(true, 11, true);
        CellStyle bodyStyle = createStyle(false, 11, true);

        // 2. Header Section (Company Name)
        Row titleRow = sheet.createRow(0);
        createCell(titleRow, 0, "GAWALI ENGINEERING AND COMPANY", titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        Row nameRow = sheet.createRow(1);
        createCell(nameRow, 0, "NAME : " + loggedInUserName, headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
        
         String currentMonthYear = LocalDate.now()
        	    .format(DateTimeFormatter.ofPattern("MMM yyyy"))
        	    .toUpperCase();

        	// 2. Create the row and cell with the dynamic title
        	Row reportTitle = sheet.createRow(2);
        	createCell(reportTitle, 0, "ATTENDANCE FOR " + currentMonthYear, headerStyle);

        	// 3. Merge the region as before
        	sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
        // 3. Table Headers
        Row tableHeader = sheet.createRow(4);
        String[] columns = {"DAYS", "DATE", "IN TIME", "OUT TIME", "DURATION", "REMARK"};
        for (int i = 0; i < columns.length; i++) {
            createCell(tableHeader, i, columns[i], headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // 4. Data Rows
        int rowCount = 5;
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        for (Timesheet ts : listTimesheets) {
            Row row = sheet.createRow(rowCount++);
            
            // Auto-fill Day (e.g., MON) using LocalDate
            String dayName = getDayFromDate(ts.getDate());
            createCell(row, 0, dayName, bodyStyle);
            
            // Format Date for display
            String dateDisplay = (ts.getDate() != null) ? ts.getDate().format(dateFmt) : "";
            createCell(row, 1, dateDisplay, bodyStyle);

            // Format Times
            String in = (ts.getTimeIn() != null) ? ts.getTimeIn().format(timeFmt) : "";
            String out = (ts.getTimeOut() != null) ? ts.getTimeOut().format(timeFmt) : "";
            createCell(row, 2, in, bodyStyle);
            createCell(row, 3, out, bodyStyle);

            // Calculate Duration: 55 min vs Hrs
            long mins = calculateMinutes(ts.getTimeIn(), ts.getTimeOut());
            String dur = (mins < 60) ? mins + " min" : String.format("%.2f hrs", mins / 60.0);
            
            createCell(row, 4, dur, bodyStyle);
            createCell(row, 5, ts.getStatus(), bodyStyle);
        }

//        int footerStart = rowCount + 2;
//        int daysInMonth = java.time.YearMonth.now().lengthOfMonth(); // Dynamic day count
//        String[] footerLabels = {"TOTAL DAYS", "ABSENT", "ATTENDED", "FULL NIGHT", "HALF NIGHT", "SUNDAY", "HOLIDAY", "TOTAL ATTENDANCE DAYS"};
//
//        for (int i = 0; i < footerLabels.length; i++) {
//            Row footerRow = sheet.createRow(footerStart + i);
//            createCell(footerRow, 4, footerLabels[i], headerStyle);
//            createCell(footerRow, 5, (i == 0) ? String.valueOf(daysInMonth) : "", bodyStyle);
//        } // Closes the FOR loop
//
//        // 6. Write to output
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//        outputStream.close();
//    }

        
        int totalAbsent = 0, totalAttended = 0, totalFullNight = 0;
        int totalHalfNight = 0, totalSunday = 0, totalHoliday = 0;

        // 2. Loop through your data rows (assuming rows 1 to rowCount contain data)
        for (int r = 1; r <= rowCount; r++) {
            Row dataRow = sheet.getRow(r);
            if (dataRow == null) continue;
            
            // Replace '5' with the actual index of your Status column
            Cell statusCell = dataRow.getCell(5); 
            if (statusCell != null) {
                String status = statusCell.getStringCellValue().toUpperCase();
                
                // Update counts based on your status labels
                switch (status) {
                    case "PRESENT": case "P": totalAttended++; break;
                    case "ABSENT":  case "A": totalAbsent++; break;
                    case "FULL NIGHT":        totalFullNight++; break;
                    case "HALF NIGHT":        totalHalfNight++; break;
                    case "SUNDAY":            totalSunday++; break;
                    case "HOLIDAY":           totalHoliday++; break;
                }
            }
        }

        // 3. Prepare Footer Labels and Values
        int footerStart = rowCount + 2;
        int daysInMonth = java.time.YearMonth.now().lengthOfMonth();

        String[] footerLabels = {"TOTAL DAYS", "ABSENT", "ATTENDED", "FULL NIGHT", "HALF NIGHT", "SUNDAY", "HOLIDAY", "TOTAL ATTENDANCE DAYS"};
        Object[] footerValues = {daysInMonth, totalAbsent, totalAttended, totalFullNight, totalHalfNight, totalSunday, totalHoliday, (totalAttended + totalFullNight + totalHalfNight)};

        // 4. Generate Footer Rows
        for (int i = 0; i < footerLabels.length; i++) {
            Row footerRow = sheet.createRow(footerStart + i);
            createCell(footerRow, 4, footerLabels[i], headerStyle);
            createCell(footerRow, 5, String.valueOf(footerValues[i]), bodyStyle);
        }

        // 5. Finalize and Write
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
    // FIXED: Now accepts LocalDate to prevent "not applicable" error
    private String getDayFromDate(LocalDate date) {
        if (date == null) return "";
        try {
            return date.getDayOfWeek().name().substring(0, 3).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    private CellStyle createStyle(boolean bold, int size, boolean border) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints((short) size);
        style.setFont(font);
        if (border) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
        }
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(value != null ? value.toString() : "");
        cell.setCellStyle(style);
    }

    private long calculateMinutes(LocalTime start, LocalTime end) {
        if (start == null || end == null) return 0;
        long diff = Duration.between(start, end).toMinutes();
        return (diff < 0) ? diff + 1440 : diff; // Handle overnight
    }
}
