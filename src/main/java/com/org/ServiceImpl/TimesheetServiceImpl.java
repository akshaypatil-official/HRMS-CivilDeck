package com.org.ServiceImpl;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

	    @Override
	    public List<Timesheet> getTimesheetsByUser(String email) {
	        User user = userRepo.findByEmail(email);
	        return timesheetRepo.findByUser(user);
	    }
	    
	    @Override
	    @Transactional
	    public void saveTimesheet(Timesheet timesheet, String email) {
	        // 1. Identify the user
	        User user = userRepo.findByEmail(email);
	        if (user == null) {
	            throw new RuntimeException("User not found: " + email);
	        }

	        LocalDate today = LocalDate.now();
	        // Use List to handle cases where the DB mistakenly has multiple records
	        List<Timesheet> existingRows = timesheetRepo.findByUserAndDate(user, today);

	        // 2. Check if a record already exists for today (Evening Logic)
	        if (!existingRows.isEmpty()) {
	            // 3. Get the first record (safely handles the previous 3-result error)
	            Timesheet current = existingRows.get(0);

	            // 4. Update timeOut only if it is provided in the request
	            if (timesheet.getTimeOut() != null) {
	                current.setTimeOut(timesheet.getTimeOut());
	            }
	            
	            // Save the updated record
	            timesheetRepo.save(current);
	            
	        } else {
	            // 5. MORNING LOGIC: Create the first entry for the day
	            timesheet.setUser(user);
	            timesheet.setDate(today);
	            timesheetRepo.save(timesheet);
	        }
	    }

		@Override
		public List<Timesheet> findAll() {
			// TODO Auto-generated method stub
			 return timesheetRepo.findAll();
		}
	    
	
}
