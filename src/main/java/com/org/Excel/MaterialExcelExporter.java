package com.org.Excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.org.Entity.Purchase;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class MaterialExcelExporter {
	 private XSSFWorkbook workbook;
	    private XSSFSheet sheet;
	    private List<Purchase> listPurchases;

	    public MaterialExcelExporter(List<Purchase> listPurchases) {
	        this.listPurchases = listPurchases;
	        workbook = new XSSFWorkbook();
	    }

	    private void writeHeaderLine() {
	        sheet = workbook.createSheet("Purchases");
	        Row row = sheet.createRow(0);

	        CellStyle style = workbook.createCellStyle();
	        XSSFFont font = workbook.createFont();
	        font.setBold(true);
	        font.setFontHeight(16);
	        style.setFont(font);

	        createCell(row, 0, "Purchase Date", style);
	        createCell(row, 1, "Full Name", style);
	        createCell(row, 2, "Company Name", style);
	        createCell(row, 3, "Site Name", style);
	        createCell(row, 4, "Material", style);
	        createCell(row, 5, "Quantity", style);
	        createCell(row, 6, "Bill No", style);
	        createCell(row, 7, "Price", style);
	    }

	    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
	        sheet.autoSizeColumn(columnCount);
	        Cell cell = row.createCell(columnCount);
	        
	        // 1. Handle Nulls first
	        if (value == null) {
	            cell.setCellValue(""); // Leave cell empty instead of crashing
	        } 
	        // 2. Handle Numbers
	        else if (value instanceof Integer) {
	            cell.setCellValue((Integer) value);
	        } else if (value instanceof Double) {
	            cell.setCellValue((Double) value);
	        } else if (value instanceof Long) {
	            cell.setCellValue((Long) value);
	        }
	        // 3. Handle everything else safely
	        else {
	            cell.setCellValue(value.toString());
	        }
	        
	        cell.setCellStyle(style);
	    }


	    private void writeDataLines() {
	        int rowCount = 1;
	        CellStyle style = workbook.createCellStyle();
	        XSSFFont font = workbook.createFont();
	        font.setFontHeight(14);
	        style.setFont(font);

	        for (Purchase purchase : listPurchases) {
	            Row row = sheet.createRow(rowCount++);
	            int columnCount = 0;

	            createCell(row, columnCount++, purchase.getDate().toString(), style);
	            String fullName = "Unknown";
	            if (purchase.getUser() != null) {
	                String firstName = purchase.getUser().getFirstName() != null ? purchase.getUser().getFirstName() : "";
	                String lastName = purchase.getUser().getLastName() != null ? purchase.getUser().getLastName() : "";
	                fullName = (firstName + " " + lastName).trim();
	            }
	            createCell(row, columnCount++, fullName.isEmpty() ? "Unknown" : fullName, style);
	            createCell(row, columnCount++, purchase.getCompanyName(), style);
	            createCell(row, columnCount++, purchase.getSiteName(), style);
	            createCell(row, columnCount++, purchase.getMaterial(), style);
	            createCell(row, columnCount++, purchase.getQuantity(), style);
	            createCell(row, columnCount++, purchase.getBillNo(), style);
	            createCell(row, columnCount++, purchase.getAmt(), style);
	        }
	    }

	    public void export(HttpServletResponse response) throws IOException {
	        writeHeaderLine();
	        writeDataLines();
	        ServletOutputStream outputStream = response.getOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	        outputStream.close();
	    }
}
