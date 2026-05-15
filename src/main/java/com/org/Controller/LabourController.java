package com.org.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.org.Entity.Labour; 
import com.org.Service.LabourService;


@Controller
@RequestMapping("/labourRecords")
public class LabourController {
    
    @Autowired
    private LabourService labourService;

    @GetMapping
    public String showForm(Model model, Principal principal) {
        // 1. Provide the LIST for a table (if needed)
        model.addAttribute("allLabours", labourService.getLaboursByUser(principal.getName()));
        
        model.addAttribute("labour", new Labour());    
        
        return "Labour_Form";
    }

    @PostMapping("/saveLabour")
    public String saveRecord(@ModelAttribute("labour") Labour labour, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; 
        }

        try {
            // This ensures the service can link the user_id
            labourService.saveLabourEntry(labour, principal.getName());
            
            return "redirect:/labourRecords?success";
        } catch (Exception e) {
            return "redirect:/labourRecords?error=" + e.getMessage();
        }
    }

}
