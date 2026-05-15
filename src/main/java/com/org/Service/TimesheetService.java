package com.org.Service;


import java.util.List;

import com.org.Entity.Timesheet;

public interface TimesheetService {

	 void saveTimesheet(Timesheet timesheet, String email);
	 
	    List<Timesheet> getTimesheetsByUser(String email);
	    
	   
		List<Timesheet> findAll();
	 
	    
	    
}
