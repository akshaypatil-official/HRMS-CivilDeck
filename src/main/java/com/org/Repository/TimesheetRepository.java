package com.org.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.Entity.Timesheet;
import com.org.Entity.User;


@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {

	 List<Timesheet> findByUser(User user);

	 List<Timesheet> findByUserAndDateBetween(String email, LocalDate start, LocalDate end);

	 Optional<Timesheet> findByUserAndDateAndTimeOutIsNull(User user, LocalDate date);
	 
	 List<Timesheet> findByUserAndDate(User user, LocalDate date);

	 Page<Timesheet> findByUser(User user, Pageable pageable);
	 
	
	    Timesheet findTopByUserOrderByDateDesc(User user);
	    
	    
	    boolean existsByUserAndDate(User user, LocalDate date);
	    
	    @Query(value = "SELECT t.* FROM timesheet t " +
                "JOIN user u ON t.user_id = u.user_id " +
                "WHERE u.email = :userId " +
                "AND YEAR(t.date) = :year " +
                "AND MONTH(t.date) = :month", 
        nativeQuery = true)
 List<Timesheet> findByUserAndMonth(
     @Param("userId") String userId, 
     @Param("year") int year, 
     @Param("month") int month
 );
	   
}
