package com.org.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.org.Entity.User;
import com.org.Repository.EmployeeRepository;
import com.org.Service.EmployeeService;



@Controller
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private  EmployeeRepository empRepository;
	
	@Autowired
	private EmployeeService employeeService;
	

	@GetMapping
    public String showEmployeeList(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        
        List<User> listEmployees;

        if (keyword != null && !keyword.isEmpty()) {
            // Search logic (requires a custom method in your Repository)
            listEmployees = empRepository.findByFirstNameContainingIgnoreCase(keyword);
        } else {
            // Fetch all employees from the database
            listEmployees = empRepository.findAll();
        }

        // Send the list to the HTML page
        model.addAttribute("listEmployees", listEmployees);
        model.addAttribute("keyword", keyword);
        System.out.print("employee list"+ listEmployees);
        return "employeelist"; 
    }
	
	@GetMapping("/delete/{user_id}")
	public String deleteEmployee(@PathVariable("user_id") Long user_id) {
	    employeeService.deleteEmployee(user_id);
	    return "redirect:/employees";
	}
} 
