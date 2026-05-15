package com.org.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.org.Service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Autowired 
	private UserService userService;

	@Bean 
	public BCryptPasswordEncoder passwordEncoder() { 
	    return new BCryptPasswordEncoder(); 
	}

	@Bean 
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { 
	    http 
	        .authorizeHttpRequests((requests) -> requests 
	            // Public assets and pages
	            .requestMatchers("/registration**", "/js/**", "/css/**", "/img/**", "/error").permitAll() 
	            // Role-based access control
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .requestMatchers("/management/**").hasAnyRole("ADMIN", "MANAGER")
	            .requestMatchers("/engineering/**").hasAnyRole("ADMIN", "ENGINEER")
	            .requestMatchers("/finance/**").hasAnyRole("ADMIN", "ACCOUNTANT")
	            // Everything else requires authentication
	            .anyRequest().authenticated() 
	        ) 
	        .formLogin((form) -> form 
	            .loginPage("/login") 
	            .defaultSuccessUrl("/timesheets", true) 
	            .permitAll() 
	        ) 
	        .logout(logout -> logout
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/login?logout")
	                .invalidateHttpSession(true) // Destroys the session
	                .deleteCookies("JSESSIONID") // Deletes the cookie
	                .permitAll()
	            );
	    return http.build(); 
	}

	@Autowired 
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { 
	    auth.userDetailsService(userService).passwordEncoder(passwordEncoder()); 
	}

}