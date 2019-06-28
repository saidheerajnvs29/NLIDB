package org.ncr.nlidb.model;


import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="card_details")


public class CardDetails {


       @Id
       @GeneratedValue(strategy = GenerationType.AUTO)
	   private int cardId;
	   private String cardNumber;
	   private String cardExpiryDate;
		private String cardStatus;
		private int productId;
		private int accountId;
		@OneToOne
		@JoinColumn(name = "accountId",
				foreignKey = @ForeignKey(name = "FK_account_id"),insertable=false,updatable=false)
		private AccountDetails accountDetails;
		@ManyToOne
		@JoinColumn(name = "productId",
		foreignKey = @ForeignKey(name = "FK_product_id"),insertable=false,updatable=false)
        private Product product;
		
		@Column(name="card_id")
		public int getCardId() {
			return cardId;
		}
		
		
		@Column(name="card_number",nullable=false,unique=true)
		public String getCardNumber() {
			return cardNumber;
		}
		
		
		
		@Column(name="card_expiry_date",nullable=false)
		public String getExpiryDate() {
			return cardExpiryDate;
		}
		
		
		@Column(name="card_status",nullable=false)
		public String getCardStatus() {
			return cardStatus;
		}
		
	
		
		@Column(name="product_id",nullable=false)
		public int getProductID() {
			return productId;
		}
		
		
		
		@Column(name="account_id",nullable=false)
		public int getAccountID() {
			return accountId;
		}
		
	@Override
		public String toString() {
			return "CardDetails [CardID=" + cardId + ", CardNumber=" + cardNumber
					+ ", ExpiryDate=" + cardExpiryDate + ", CardStatus=" + cardStatus + ", ProductID=" + productId
					+ ", AccountID=" + accountId + "]";
		}

}
