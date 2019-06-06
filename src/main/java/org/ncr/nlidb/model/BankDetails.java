package org.ncr.nlidb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "bank_branch_details")
public class BankDetails 
{
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int branchId;
	private String branchName;
	private String branchAddress;
	
	@Column(name="branch_id")
	public int getBranchId() {
		return branchId;
	}
	
	@Column(name="branch_name",nullable=false,unique=true)
	public String getBranchName() {
		return branchName;
	}
	
	@Column(name="branch_address",nullable=false,unique=true)
	public String getBranchAddress() {
		return branchAddress;
	}

	@Override
	public String toString() {
		return "BankDetails [branchId=" + branchId + ", branchName=" + branchName + ", branchAddress=" + branchAddress + "]";
	}


}