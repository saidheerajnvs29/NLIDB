package org.ncr.nlidb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product {
	
	@Id
	private int productId;
	private String productDescription;
	private String productCode;
	
	@Column(name="product_id")
	public int getProductId() {
		return productId;
	}
	
	@Column(name="product_description",nullable=false)
	public String getDescription() {
		return productDescription;
	}
	
	@Column(name="product_code",nullable=false)
	public String getCode() {
		return productCode;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", description=" + productDescription + ", code=" + productCode + "]";
	}
	
	
}