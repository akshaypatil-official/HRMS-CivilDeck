package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.Entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);
	
//	 @Procedure(procedureName = "emp_Timesheet_list")
//	    List<User> searchByNamesProc(@Param("firstName") String firstName, 
//	                                @Param("lastName") String lastName);
	 
	 @Query(value = "CALL emp_Timesheet_list(:firstName, :lastName)", nativeQuery = true)
	    List<Object[]> findTimesheetData(@Param("firstName") String firstName, 
	                                     @Param("lastName") String lastName);

	    
	    @Query(value = "SELECT c.name FROM user u " +
                "JOIN companies c ON u.company_id = c.id " +
                "WHERE u.user_id = :user_id", nativeQuery = true)
 String findCompanyNameByUser_Id(@Param("user_id") Long user_Id);

	

}
