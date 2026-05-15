package com.org.Service;

import java.util.List;


import com.org.Entity.Purchase;

public interface PurchaseService {

	
    List<Purchase> getAllPurchases();
    
    Purchase getPurchaseById(long id);
    
    void deletePurchase(long id);

	List<Purchase> getPurchasesByUser(String email);

	void savePurchase(Purchase purchase, String name);

	List<Purchase> findAll();

	
}
