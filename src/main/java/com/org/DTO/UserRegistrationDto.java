package com.org.DTO;

public class UserRegistrationDto {

	    private String firstName;
	    private String lastName;
	    private String email;
	    private String password;
	    private String dob;
	    private String doj;
	    private String phoneNo;
	    private String designation;
	    private String address;
	    private String gender;
	    private String role_Id;
	    public UserRegistrationDto() {
	    	
	    }

		public UserRegistrationDto(String firstName, String lastName, String dob,
				String doj, String phoneNo, String designation, String address, String gender,String email, String password, String role_Id) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.password = password;
			this.dob = dob;
			this.doj = doj;
			this.phoneNo = phoneNo;
			this.designation = designation;
			this.address = address;
			this.gender = gender;
			this.role_Id = role_Id;
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

		public String getRole_Id() {
			return role_Id;
		}

		public void setRole_Id(String role_Id) {
			this.role_Id = role_Id;
		}

    
}
