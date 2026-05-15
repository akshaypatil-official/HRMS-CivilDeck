package com.org.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_purchse")
public class Purchase {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name = "purchase_id")
	private long p_Id;

	@Column(name = "purchase_date")
	private LocalDate date;

	@Column(name = "in_time")
	private LocalTime InTime;

	@Column(name = "company_name")
	private String CompanyName;

	@Column(name = "site_name")
	private String SiteName;

	@Column(name = "purchase_material")
	private String Material;

	@Column(name = "qty")
	private Double  Quantity;

	@Column(name = "bill_no")
	private String BillNo;

	@Column(name = "price")
	private Double  Amt;

	@Column(name = "unit")
	private String unit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public long getP_Id() {
		return p_Id;
	}

	public void setP_Id(long p_Id) {
		this.p_Id = p_Id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getInTime() {
		return InTime;
	}

	public void setInTime(LocalTime inTime) {
		InTime = inTime;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getSiteName() {
		return SiteName;
	}

	public void setSiteName(String siteName) {
		SiteName = siteName;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	public Double  getQuantity() {
		return Quantity;
	}

	public void setQuantity(Double  quantity) {
		Quantity = quantity;
	}

	public String getBillNo() {
		return BillNo;
	}

	public void setBillNo(String billNo) {
		BillNo = billNo;
	}

	public Double  getAmt() {
		return Amt;
	}

	public void setAmt(Double  amt) {
		Amt = amt;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
}
