package com.org.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Entity.Purchase;
import com.org.Entity.User;
import com.org.Repository.PurchaseRepository;
import com.org.Repository.UserRepository;
import com.org.Service.PurchaseService;

@Service
public class PurchaseServiceImpl implements PurchaseService {

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private UserRepository userRepo;

	
	@Override
	public List<Purchase> getPurchasesByUser(String email) {
		User user = userRepo.findByEmail(email);
		return purchaseRepository.findByUser(user);
	}

	@Override
	public List<Purchase> getAllPurchases() {
		return purchaseRepository.findAll();
	}

	@Override
	public Purchase getPurchaseById(long id) {
		return purchaseRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Purchase not found with id: " + id));
	}

	@Override
	public void deletePurchase(long id) {
		purchaseRepository.deleteById(id);
	}

	 @Override
	    public void savePurchase(Purchase purchase, String username) {
	        // 1. Fetch the user from the DB using the name from Principal
	        User user = userRepo.findByEmail(username); // or findByUsername
	        
	        if (user == null) {
	            throw new RuntimeException("User not found in database");
	        }
	        purchase.setUser(user);
	        // 3. Now save
	        purchaseRepository.save(purchase);
	    }

	 @Override
	    public List<Purchase> findAll() {
	   
	        return purchaseRepository.findAll();
	    }
}
