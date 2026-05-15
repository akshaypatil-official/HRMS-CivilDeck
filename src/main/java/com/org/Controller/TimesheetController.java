package com.org.Controller;


import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.org.Entity.Timesheet;
import com.org.Excel.TimesheetExcelExporter;
import com.org.Service.TimesheetService;
import com.org.Service.UserService;

import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/timesheets")
public class TimesheetController {

	@Autowired
    private TimesheetService timesheetService;
	
	@Autowired
	private UserService userService;

    @GetMapping
    public String showTimesheetPage(Model model, Principal principal) {
        model.addAttribute("timesheets", timesheetService.getTimesheetsByUser(principal.getName()));
        model.addAttribute("timesheet", new Timesheet());
        return "Timesheet";
    }

    @PostMapping("/save")
    public String saveTimesheet(@ModelAttribute("timesheet") Timesheet timesheet, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Redirect if session is lost
        }

        try {
            timesheetService.saveTimesheet(timesheet, principal.getName());
            return "redirect:/timesheets?success";
        } catch (Exception e) {
            return "redirect:/timesheets?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchLogs(
            @RequestParam(required = false, defaultValue = "") String firstName,
            @RequestParam(required = false, defaultValue = "") String lastName) {
        
        List<Map<String, Object>> results = userService.findByName(firstName, lastName);
        return ResponseEntity.ok(results);
    }
      
    
    @GetMapping("/export")
    public void exportTimesheet(HttpServletResponse response, Principal principal) throws IOException {
        if (principal == null) {
            response.sendRedirect("/login");
            return;
        }

        // 1. Fetch data from service
        List<Timesheet> listTimesheets = timesheetService.findAll(); 

        // 2. Fetch logged-in user names from userService
        String firstName = userService.getFirstName();
        String lastName = userService.getLastName();
        String currentUserName = firstName + " " + lastName;

        System.out.println("Exporting for user: " + currentUserName);

        // 3. Set response headers
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Timesheet_Report.xlsx");

        // 4. Pass list and dynamic name to the Exporter
        TimesheetExcelExporter exporter = new TimesheetExcelExporter(listTimesheets, currentUserName);
        exporter.export(response);
    }


}
    
