package com.org.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.org.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
				// 1. Intercept every single request to check if the user was deleted from the
				// DB
				.addFilterAfter(new OncePerRequestFilter() {
					@Override
					protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
							jakarta.servlet.FilterChain filterChain)
							throws jakarta.servlet.ServletException, IOException {

						String path = request.getRequestURI();

						// Skip checks for static assets and public landing pages to prevent infinite
						// redirect loops
						if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
								|| path.equals("/login") || path.equals("/force-logout") || path.equals("/error")) {
							filterChain.doFilter(request, response);
							return;
						}

						Authentication auth = SecurityContextHolder.getContext().getAuthentication();

						// If a user has an active session cookie but isn't anonymous
						if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
							try {
								// Try loading user details from database
								userService.loadUserByUsername(auth.getName());
							} catch (UsernameNotFoundException e) {
								// Catch exception if user was dropped from database, clear context, and kick
								// them out
								HttpSession session = request.getSession(false);
								if (session != null) {
									session.invalidate();
								}
								SecurityContextHolder.clearContext();
								response.sendRedirect(request.getContextPath() + "/login?error=account_deleted");
								return;
							}
						}
						filterChain.doFilter(request, response);
					}
				}, AuthorizationFilter.class)

				// 2. Your original endpoint rules configuration
				.authorizeHttpRequests((requests) -> requests
						// Public assets and pages
						.requestMatchers("/css/**", "/js/**", "/images/**", "/error/**").permitAll()
						// Role-based access control
						.requestMatchers("/employees").hasAnyAuthority("admin", "manager", "engineer")
						// Everything else requires authentication
						.anyRequest().authenticated())
				.formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/timesheets", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true) // Destroys the session
						.deleteCookies("JSESSIONID") // Deletes the cookie
						.permitAll());

		return http.build();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}
}