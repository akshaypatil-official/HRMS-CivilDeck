package com.org.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.org.Entity.Timesheet;

public interface TimesheetService {

	 void saveTimesheet(Timesheet timesheet, String email);
	 
//	    List<Timesheet> getTimesheetsByUser(String email);
	    
	   
		List<Timesheet> findAll();
	 

		Page<Timesheet> getTimesheetsByUser(String email, Pageable pageable);

		boolean hasUserLoggedTimeForDate(String username, LocalDate today);
		

		List<Timesheet> findByUserAndMonth(String userId, int year, int month);

		void updateTimesheet(Timesheet timesheet);

		Timesheet getTimesheetById(Long id);
	
		
	
	    
}
