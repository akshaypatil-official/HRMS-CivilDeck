package com.org.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.org.Entity.Purchase;
import com.org.Excel.MaterialExcelExporter;
import com.org.Service.PurchaseService;

import jakarta.servlet.http.HttpServletResponse;



@Controller
@RequestMapping("/purchases")
public class PurchaseController {

	 @Autowired
	 private PurchaseService purchaseService;
	 
	    
	    @GetMapping
	    public String showForm(Model model, Principal principal) {
	    	model.addAttribute("purchases", purchaseService.getPurchasesByUser(principal.getName()));
	        model.addAttribute("purchase", new Purchase());
	        return "Purchase"; // Your HTML file name
	    }
	    
	    
	    
	    @PostMapping("/save")
	    public String savePurchase(@ModelAttribute("purchase") Purchase purchase, Principal principal) {
	        if (principal == null) return "redirect:/login";

	        try {
	            purchaseService.savePurchase(purchase, principal.getName());
	            return "redirect:/purchases?success";
	        } catch (Exception e) {
	            // Log the error on the server side so you can see the details
	            e.printStackTrace(); 
	            // Redirect with a simple keyword to avoid "Invalid Character" errors
	            return "redirect:/purchases?error=db_error"; 
	        }
	    }


	    @GetMapping("/export-excel")
	    public void exportToExcel(HttpServletResponse response) throws IOException {
	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=MaterialList.xlsx");
	        
	        List<Purchase> listPurchases = purchaseService.findAll(); // Get your data
	        MaterialExcelExporter exporter = new MaterialExcelExporter(listPurchases);
	        exporter.export(response);
	    }
	    
	    
	    @DeleteMapping("/{id}")
	    @ResponseBody
	    public String delete(@PathVariable long id) {
	        purchaseService.deletePurchase(id);
	        return "Deleted successfully";
	    }
}
