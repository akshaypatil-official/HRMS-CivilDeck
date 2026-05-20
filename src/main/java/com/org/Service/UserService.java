package com.org.Service;


import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetailsService;
import com.org.DTO.UserRegistrationDto;
import com.org.Entity.User;


public interface UserService extends UserDetailsService{
    
	User save(UserRegistrationDto registrationDto);
	
	User getUserByEmail(String email);

	List<Map<String, Object>> findByName(String firstName, String lastName);

//	Timesheet name 
	
	String getFirstName();
    String getLastName();

    List<String> getAllActiveUsernames();

    
}
