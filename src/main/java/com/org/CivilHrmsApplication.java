package com.org;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.org.Entity.Company;
import com.org.Repository.CompanyRepository;

@SpringBootApplication
public class CivilHrmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivilHrmsApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner initData(CompanyRepository companyRepo) {
        return args -> {
            if (!companyRepo.existsByName("Civildeck")) {
                companyRepo.save(new Company("Civildeck"));
            }
            if (!companyRepo.existsByName("Gawali Enterprises")) {
                companyRepo.save(new Company("Gawali Enterprises"));
            }
        };
    }

}
