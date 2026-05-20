package com.org.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.org.Entity.Timesheet;
import com.org.Service.TimesheetService;
import com.org.Service.UserService;

@Component
public class TimesheetScheduler {

	
	 private final UserService userService;
	    private final TimesheetService timesheetService;

	    public TimesheetScheduler(UserService userService, TimesheetService timesheetService) {
	        this.userService = userService;
	        this.timesheetService = timesheetService;
	    }

	    // Fires every night at 11:59:00 PM
	    @Scheduled(cron = "0 59 23 * * *")
	    public void autoFillAbsentRecords() {
	        LocalDate today = LocalDate.now();
	        List<String> usernames = userService.getAllActiveUsernames();

	        for (String username : usernames) {
	            boolean hasLoggedTime = timesheetService.hasUserLoggedTimeForDate(username, today);
	            
	            if (!hasLoggedTime) {
	                Timesheet absentRecord = new Timesheet();
	                absentRecord.setDate(today);
	                absentRecord.setStatus("Absent");
	                absentRecord.setTimeIn(LocalTime.parse("00:00"));  
	                absentRecord.setTimeOut(LocalTime.parse("00:00"));
	                absentRecord.setDescription("Absent");
	                
	                timesheetService.saveTimesheet(absentRecord, username);
	            }
	        }
	    }
}
