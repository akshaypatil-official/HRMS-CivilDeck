package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.Entity.Labour;
import com.org.Entity.User;

public interface LabourRepository extends JpaRepository<Labour, Long>{

	List<Labour> findByUser(User user);
}
