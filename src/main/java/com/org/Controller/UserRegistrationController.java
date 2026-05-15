package com.org.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.org.DTO.UserRegistrationDto;
import com.org.Entity.Role;
import com.org.Repository.RoleRepository;
import com.org.Service.UserService;



@Controller
@RequestMapping("/registrations")
public class UserRegistrationController {
   
	    @Autowired
	    private UserService userService;
        
	    private final RoleRepository roleRepository;
	    
	    public UserRegistrationController(UserService userService, RoleRepository roleRepository) {
			super();
			this.userService = userService;
			this.roleRepository=roleRepository;
		}
	    
	    @ModelAttribute("user")
	    public UserRegistrationDto userRegistrationDto() {
	    	return new UserRegistrationDto();
	    }
	    
	    @GetMapping
	    public String showRegistrationForm(Model model) {
	        List<Role> roles = roleRepository.findAll();
	        model.addAttribute("allRoles", roles);
	        return "registration";
	    }


	    @PostMapping
	    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
	        userService.save(registrationDto);
	        return "redirect:/employees";
	    }
    
	   
}
