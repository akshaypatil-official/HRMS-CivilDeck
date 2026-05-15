package com.org.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Entity.User;
import com.org.Repository.EmployeeRepository;
import com.org.Service.EmployeeService;


@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository empRepository;
	
	@Override
    public List<User> getEmployeesByKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return empRepository.findByFirstNameContainingIgnoreCase(keyword);
        }
        return empRepository.findAll();
    }


	@Override
    public void deleteEmployee(Long user_id) {
        empRepository.executeDelete(user_id);
    }
}
