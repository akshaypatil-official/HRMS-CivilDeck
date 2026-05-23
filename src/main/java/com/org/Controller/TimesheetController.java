package com.org.Controller;


import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.org.Entity.Timesheet;
import com.org.Entity.User;
import com.org.Excel.TimesheetExcelExporter;
import com.org.Service.TimesheetService;
import com.org.Service.UserService;

import jakarta.servlet.http.HttpServletResponse;


@Controller
@CrossOrigin
@RequestMapping("/timesheets")
public class TimesheetController {

	
	@Autowired
    private TimesheetService timesheetService;
	
	@Autowired
	private UserService userService;
	
	 @GetMapping
	    public String showTimesheetPage(
	            Model model, 
	            Principal principal, 
	            @RequestParam(defaultValue = "0") int page) {
	        
	        int pageSize = 10;
	        Pageable pageable = PageRequest.of(page, pageSize);
	        
	        // 1. Declare and fetch the paginated page object using the principal name
	        Page<Timesheet> timesheetPage = timesheetService.getTimesheetsByUser(principal.getName(), pageable);
	        
	        // 2. Add structural pagination metadata to model
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", timesheetPage.getTotalPages());
	        
	        // 3. Extract and pass ONLY the list of 10 items for the current page
	        model.addAttribute("timesheets", timesheetPage.getContent());
	       
	        // 4. Pass empty object for your form binding
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
    public void exportTimesheet(
        @RequestParam(name = "month", required = false) String month,
        HttpServletResponse response, 
        Principal principal
    ) throws IOException {
        
        // 1. Force login if session expired
        if (principal == null) {
            response.sendRedirect("/login");
            return;
        }
        
        // 2. Validate input format (expects YYYY-MM)
        if (month == null || month.trim().isEmpty() || !month.matches("^\\d{4}-\\d{2}$")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format. Use YYYY-MM.");
            return;
        }

        // 3. Parse data securely
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);

        // 4. Get the logged-in Username/Email from principal
        String loggedInUserEmail = principal.getName(); 

        // 5. FETCH LOGIN USER ID: Use the email to get the actual User object from your database
        User loggedInUser = userService.getUserByEmail(loggedInUserEmail); // Or findByUsername depending on your setup
        
        if (loggedInUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User details not found.");
            return;
        }
        
        Long user_Id = loggedInUser.getUser_id(); // This extracts the actual logged-in user's ID dynamically

        // 6. Fetch ONLY filtered rows from the database
        List<Timesheet> listTimesheets = timesheetService.findByUserAndMonth(loggedInUserEmail, year, monthValue); 

        // 7. Fetch details using the dynamically retrieved user_Id
        String companyName = userService.getCompanyName(user_Id); 
        if (companyName == null) {
            companyName = "Default Company";
        }

        String firstName = loggedInUser.getFirstName(); // Safer to pull directly from the retrieved user object
        String lastName = loggedInUser.getLastName();
        
        if (firstName == null) firstName = "User";
        if (lastName == null) lastName = "";

        String loggedInUserName = (firstName + " " + lastName).trim();
        String safeFileName = loggedInUserName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String finalFileName = "Timesheet_" + safeFileName + "_" + month + ".xlsx";
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");

        TimesheetExcelExporter exporter = new TimesheetExcelExporter(listTimesheets,companyName, loggedInUserName, month);
        exporter.export(response);
    }


}
    
