package com.org.Service;



import java.util.List;
import com.org.Entity.Labour;


public interface LabourService {

	 List<Labour> getLaboursByUser(String email);

	 void saveLabourEntry(Labour record, String email);

	
}
