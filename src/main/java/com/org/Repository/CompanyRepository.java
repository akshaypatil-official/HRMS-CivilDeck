package com.org.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.Entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{
	boolean existsByName(String name);
}
