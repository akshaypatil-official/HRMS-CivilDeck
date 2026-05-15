package com.org.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Entity.Labour;
import com.org.Entity.User;
import com.org.Repository.LabourRepository;
import com.org.Repository.UserRepository;
import com.org.Service.LabourService;

import jakarta.transaction.Transactional;

@Service
public class LabourServiceImpl implements LabourService{
	
	 @Autowired
	    private LabourRepository labourRepository;
	 
	 @Autowired
	    private UserRepository userRepo;
	 
	 @Override
	 public List<Labour> getLaboursByUser(String email) {
	        User user = userRepo.findByEmail(email);
	        return labourRepository.findByUser(user);
	    }
	 
	 @Override
	 @Transactional
	 public void saveLabourEntry(Labour record, String email) {
	     
	     User user = userRepo.findByEmail(email);
	     
	     if (user == null) {
	         throw new RuntimeException("User not found: " + email);
	     }
	     record.setUser(user);

	     labourRepository.save(record);
	 }
}
