package org.ncr.nlidb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.ForeignKey;

@Entity
@Table(name="account_details")
public class AccountDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int accountId;
	private String accountNumber;
	private String accountType;
	private int accountBalance;
	private int customerId;
	private int branchId;
	
	@Column(name="account_id")
	public int getAccountId() {
		return accountId;
	}
	
	@Column(name="account_number",nullable=false,unique=true)
	public String getAccountNumber() {
		return accountNumber;
	}
	
	@Column(name="account_type",nullable=false)
	public String getAccountType() {
		return accountType;
	}
	
	@Column(name="account_balance",nullable=false)
	public int getBalance() {
		return accountBalance;
	}
	
	@Column(name="customer_id",nullable=false,unique=true)
	public int getCustomerId() {
		return customerId;
	}
	
	@Column(name="branch_id",nullable=false)
	public int getBranchId() {
		return branchId;
	}
	
	
	@OneToOne
	@JoinColumn(name="customerId",foreignKey=@ForeignKey(name="FK_customer_id"),insertable = false, updatable = false)
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "branchId",foreignKey = @ForeignKey(name = "FK_branch_id"),insertable = false, updatable = false)
	private BankDetails bankdetails;
	
	

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", accountNumber=" + accountNumber + ", accountType=" + accountType
				+ ", balance=" + accountBalance + ", customerId=" + customerId + ", branchId=" + branchId + "]";
	}
	
	
	
    
}