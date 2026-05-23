package com.org.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.org.Entity.Timesheet;
import com.org.Entity.User;
import com.org.Repository.TimesheetRepository;
import com.org.Repository.UserRepository;
import com.org.Service.TimesheetService;
import jakarta.transaction.Transactional;


@Service
public class TimesheetServiceImpl implements TimesheetService{

	 @Autowired
	    private TimesheetRepository timesheetRepo;

	    @Autowired
	    private UserRepository userRepo;

//	    @Override
//	    public void saveTimesheet(Timesheet timesheet, String email) {
//	        User user = userRepo.findByEmail(email); // Or findByUsername
//	        timesheet.setUser(user);
//	        timesheetRepo.save(timesheet);
//	    }

	    
//	    @Override
//	    public List<Timesheet> getTimesheetsByUser(String email) {
//	        User user = userRepo.findByEmail(email);
//	        return timesheetRepo.findByUser(user);
//	    }
//	    
	    
	    
	    @Override
	    public Page<Timesheet> getTimesheetsByUser(String email, Pageable pageable) {
	        User user = userRepo.findByEmail(email);
	        if (user == null) {
	            return Page.empty(pageable);
	        }

	        // Auto-check the last 7 days for any missing entries (including yesterday)
	        LocalDate today = LocalDate.now();
	        for (int i = 1; i <= 2; i++) {
	            LocalDate checkDate = today.minusDays(i);
	            
	            // Check if a record already exists for this past date
	            boolean exists = timesheetRepo.existsByUserAndDate(user, checkDate);
	            
	            // If missing, automatically fill it as "Absent"
	            if (!exists) {
	                Timesheet absentRecord = new Timesheet();
	                absentRecord.setUser(user);
	                absentRecord.setDate(checkDate);
	                absentRecord.setStatus("Absent");
	                absentRecord.setTimeIn(LocalTime.parse("00:00"));  
	                absentRecord.setTimeOut(LocalTime.parse("00:00"));
	                
	                
	                timesheetRepo.save(absentRecord);
	            }
	        }

	        // Return the paginated entries with newest dates at the top
	        return timesheetRepo.findByUser(user, pageable);
	    }
	    
//	    
//	    @Override
//	    @Transactional
//	    public void saveTimesheet(Timesheet timesheet, String email) {
//	        // 1. Identify the user
//	        User user = userRepo.findByEmail(email);
//	        if (user == null) {
//	            throw new RuntimeException("User not found: " + email);
//	        }
//
//	        LocalDate today = LocalDate.now();
//	        // Use List to handle cases where the DB mistakenly has multiple records
//	        List<Timesheet> existingRows = timesheetRepo.findByUserAndDate(user, today);
//
//	        // 2. Check if a record already exists for today (Evening Logic)
//	        if (!existingRows.isEmpty()) {
//	            // 3. Get the first record (safely handles the previous 3-result error)
//	            Timesheet current = existingRows.get(0);
//
//	            // 4. Update timeOut only if it is provided in the request
//	            if (timesheet.getTimeOut() != null) {
//	                current.setTimeOut(timesheet.getTimeOut());
//	            }
//	            
//	            // Save the updated record
//	            timesheetRepo.save(current);
//	            
//	        } else {
//	            // 5. MORNING LOGIC: Create the first entry for the day
//	            timesheet.setUser(user);
//	            timesheet.setDate(today);
//	            timesheetRepo.save(timesheet);
//	        }
//	    }

	    @Override
	    @Transactional
	    public void saveTimesheet(Timesheet timesheet, String email) {
	        // 1. Identify the user
	        User user = userRepo.findByEmail(email);
	        if (user == null) {
	            throw new RuntimeException("User not found: " + email);
	        }

	        LocalDate today = LocalDate.now();

	        // 2. AUTO-ABSENT LOGIC: Only trigger this during Morning Check-In (when no entry exists for today)
	        List<Timesheet> existingRows = timesheetRepo.findByUserAndDate(user, today);
	        
	        if (existingRows.isEmpty()) {
	            // Find the most recent timesheet entry this user saved in the past
	            Timesheet latestEntry = timesheetRepo.findTopByUserOrderByDateDesc(user);
	            
	            if (latestEntry != null) {
	                LocalDate lastSavedDate = latestEntry.getDate();
	                // Calculate how many days passed between the last entry and today
	                long missingDays = ChronoUnit.DAYS.between(lastSavedDate, today);

	                // If there is a gap of 2 or more days, fill the gap with Absent records
	                // Example: Last entry Friday, today is Monday. Missing days = 3. Fills Saturday & Sunday.
	                if (missingDays > 1) {
	                    for (int i = 1; i < missingDays; i++) {
	                        LocalDate gapDate = lastSavedDate.plusDays(i);
	                        
	                        Timesheet absentRecord = new Timesheet();
	                        absentRecord.setUser(user);
	                        absentRecord.setDate(gapDate);
	                        absentRecord.setStatus("Absent");
	          
	                        timesheetRepo.save(absentRecord);
	                    }
	                }
	            }
	        }

	        // 3. EVENING LOGIC: Update timeOut if record exists for today
	        if (!existingRows.isEmpty()) {
	            Timesheet existingRecord = existingRows.get(0);

	            if (timesheet.getTimeOut() != null) {
	            	existingRecord.setTimeOut(timesheet.getTimeOut());
	                // Optional: If you track status or hours, you can update them from 'Present' here
	            }
	            
//	            timesheetRepo.save(current);
	            if (timesheet.getNightTimeOut() != null) {
	                existingRecord.setNightTimeOut(timesheet.getNightTimeOut());
	                existingRecord.setNightStatus(timesheet.getNightStatus()); // Saves 'Full Night' or 'Half Night'
	                existingRecord.setStatus(timesheet.getStatus());           // Saves 'DayNight'
	            }

	            // Handle case where Night In is stamped later in the day on an existing record
	            if (timesheet.getNightTimeIn() != null && existingRecord.getNightTimeIn() == null) {
	                existingRecord.setNightTimeIn(timesheet.getNightTimeIn());
	                existingRecord.setNightStatus(timesheet.getNightStatus());
	                existingRecord.setStatus(timesheet.getStatus());
	            }
	            
	            timesheetRepo.save(existingRecord);
	            
	        } else {
	            // 4. MORNING LOGIC: Create the primary entry for today
	            timesheet.setUser(user);
	            timesheet.setDate(today);
	            
	            // Ensure today's fresh morning record defaults to "Present" or active status
	            if ("DayNight".equals(timesheet.getStatus())) {
	                // Starting a night shift as a fresh record
	                timesheet.setTimeIn(null);
	                timesheet.setTimeOut(null);
	            } else {
	                // Starting a regular morning shift or default
	                if (timesheet.getStatus() == null) {
	                    timesheet.setStatus("Present");
	                }
	                timesheet.setNightStatus(null);
	                timesheet.setNightTimeIn(null);
	                timesheet.setNightTimeOut(null);
	            }
	            
	            timesheetRepo.save(timesheet);
	        }
	    }

		@Override
		public List<Timesheet> findAll() {
			// TODO Auto-generated method stub
			 return timesheetRepo.findAll();
		}

		@Override
		public boolean hasUserLoggedTimeForDate(String email, LocalDate today) {
		    // 1. Find the user by email first
		    User user = userRepo.findByEmail(email);
		    
		    // 2. If the user doesn't exist, they haven't logged time
		    if (user == null) {
		        return false;
		    }
		    
		    // 3. Query the repository using the User object and the date
		    return timesheetRepo.existsByUserAndDate(user, today);
		}

		@Override
	    public List<Timesheet> findByUserAndMonth(String userId, int year, int month) {
	        // Logs the parameters to the console for easy debugging
	        System.out.println("Service Layer -> Fetching data for User: " + userId + ", Year: " + year + ", Month: " + month);
	        
	        // Sends the parameters straight to the database query
	        return timesheetRepo.findByUserAndMonth(userId, year, month);
	    }
	   
}
