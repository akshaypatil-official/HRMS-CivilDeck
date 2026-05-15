package com.org.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.DTO.UserRegistrationDto;
import com.org.Entity.Role;
import com.org.Entity.User;
import com.org.Repository.RoleRepository;
import com.org.Repository.UserRepository;
import com.org.Service.UserService;

import jakarta.transaction.Transactional;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    
    private final RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,RoleRepository roleRepository) {
        super();
        this.userRepository = userRepository;
        this.roleRepository=roleRepository;
    }

    @Override 
    public User save(UserRegistrationDto dto) {
        // 1. Get the Role
        Long roleId = Long.parseLong(dto.getRole_Id());
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role ID " + roleId + " not found"));

        // 2. Create and Save User
        User user = new User(
            dto.getFirstName(), 
            dto.getLastName(), 
            dto.getDob(), 
            dto.getDoj(), 
            dto.getPhoneNo(), 
            dto.getDesignation(), 
            dto.getAddress(), 
            dto.getGender(), 
            dto.getEmail(), 
            passwordEncoder.encode(dto.getPassword()), 
            Arrays.asList(role)
        );

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), 
        		user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .filter(role -> role != null && role.getName() != null && !role.getName().trim().isEmpty())
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public User getUserByEmail(String email) {
        // Calling your repository method
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            // Log it for debugging
            System.out.println("Service: User not found for email: " + email);
        }        
        return user;
    }
    

    @Override
    @Transactional
    public  List<Map<String, Object>>  findByName(String firstName, String lastName) {
        // Call the repository method
        List<Object[]> results = userRepository.findTimesheetData(firstName, lastName);
        
        List<Map<String, Object>> mappedResults = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            // The order here MUST match the SELECT order in your SQL procedure
            map.put("user_id", row[0]);
            map.put("first_name", row[1]);
            map.put("last_name", row[2]);
            map.put("address", row[3]);
            map.put("designation", row[4]);
            map.put("gender", row[5]);
            map.put("email", row[6]);
            map.put("password", row[7]);
            map.put("date_of_birth", row[8]);
            map.put("date_of_join", row[9]);
            map.put("phone_no", row[10]);
            map.put("date", row[11]);
            map.put("time_in", row[12]);
            map.put("time_out", row[13]);
            map.put("location", row[14]);
            map.put("description", row[15]);
            map.put("status", row[16]);
            map.put("role_id", row[17]);
            mappedResults.add(map);
        }

        return mappedResults;
    }

    @Override
    public String getFirstName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email); // Returns User object
            if (user != null) {
                return user.getFirstName();
            }
        }
        return "Guest";
    }

    @Override
    public String getLastName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return user.getLastName();
            }
        }
        return "";
    }
    
	/*
	 * @Override
	 * 
	 * @Transactional public List<User> findByName(String firstName, String
	 * lastName) { String fName = (firstName != null && !firstName.isEmpty()) ?
	 * firstName : null; String lName = (lastName != null && !lastName.isEmpty()) ?
	 * lastName : null;
	 * 
	 * return userRepository.searchByNamesProc(fName, lName); }
	 */
}	