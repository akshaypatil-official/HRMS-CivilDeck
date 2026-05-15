package com.org.Service;

import java.util.List;

import com.org.Entity.User;

public interface EmployeeService {
	
	List<User> getEmployeesByKeyword(String keyword);
	

	void deleteEmployee(Long user_id);
	
}
