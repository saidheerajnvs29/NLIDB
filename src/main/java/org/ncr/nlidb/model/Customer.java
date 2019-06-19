package org.ncr.nlidb.model; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int customerId;
	private String customerName;
	private String customerAddress;
	private String customerEmail;
	private String customerPhone;
	
	@Column(name="customer_id")
	public int getCustomerId() {
		return customerId;
	}
	
	@Column(name="customer_name")
	public String getCustomerName() {
		return customerName;
	}
	
	@Column(name="customer_address")
	public String getCustomerAddress() {
		return customerAddress;
	}
	
	@Column(name="customer_email", unique = true)
	public String getCustomerEmail() {
		return customerEmail;
	}
	
	@Column(name="customer_phone", nullable=false,unique = true)
	public String getCustomerPhone() {
		return customerPhone;
	}

	@Override
	public String toString() {
		return "Customer [cutomerid=" + customerId + ", customerName=" + customerName + ", customerAddress="
				+ customerAddress + ", customerEmail=" + customerEmail + ", customerPhone=" + customerPhone + "]";
	}
	
	
}