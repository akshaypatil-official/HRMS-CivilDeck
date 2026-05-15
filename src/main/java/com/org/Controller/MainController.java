package com.org.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@GetMapping("/login")
	public String login() {
	    return "login"; 
	}

	 @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        // Clear the session data
	        session.invalidate(); 
	        // Redirect to the login page with a success message (optional)
	        return "redirect:/login?logout=true";
	    }
}
