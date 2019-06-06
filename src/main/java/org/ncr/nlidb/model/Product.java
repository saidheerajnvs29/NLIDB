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
	private int productDescription;
	private int productCode;
	
	@Column(name="product_id")
	public int getProductId() {
		return productId;
	}
	
	@Column(name="product_description",nullable=false)
	public int getDescription() {
		return productDescription;
	}
	
	@Column(name="product_code",nullable=false)
	public int getCode() {
		return productCode;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", description=" + productDescription + ", code=" + productCode + "]";
	}
	
	
}