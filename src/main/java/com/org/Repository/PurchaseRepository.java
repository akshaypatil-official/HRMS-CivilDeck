package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.Entity.Purchase;
import com.org.Entity.User;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{

	List<Purchase> findByUser(User user);

}
