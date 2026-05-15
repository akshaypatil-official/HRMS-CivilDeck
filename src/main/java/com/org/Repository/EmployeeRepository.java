package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.org.Entity.User;

@Repository
public interface EmployeeRepository extends JpaRepository<User, Long>{

	 List<User> findByFirstNameContainingIgnoreCase(String keyword);

	 List<User> findAll();

	 @Modifying
	    @Transactional
	    @Query("DELETE FROM User e WHERE e.user_id = :id")
	    void executeDelete(@Param("id") Long user_id); 

}
