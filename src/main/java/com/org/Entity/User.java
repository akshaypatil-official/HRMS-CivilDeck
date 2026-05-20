package com.org.Entity;

import java.util.Collection;
import java.util.Optional;

import jakarta.persistence.*;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long user_id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "date_of_birth")
	private String dob;

	@Column(name = "date_of_join")
	private String doj;

	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "designation")
	private String designation;

	@Column(name = "address")
	private String address;

	@Column(name = "gender")
	private String gender;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "company_id", nullable = true) // Changed to true temporarily
	private Company company;
	
	 public User() {
	    }
	 
	// Parameterized Constructor
	public User(Long user_id, String firstName, String lastName, String dob, String doj, String phoneNo,
			String designation, String address, String gender, String email, String password, Collection<Role> roles,
			Company company) {
		super();
		this.user_id = user_id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.doj = doj;
		this.phoneNo = phoneNo;
		this.designation = designation;
		this.address = address;
		this.gender = gender;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.company = company;
	}

	// Getters and Setters
	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getDoj() {
		return doj;
	}

	public void setDoj(String doj) {
		this.doj = doj;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
