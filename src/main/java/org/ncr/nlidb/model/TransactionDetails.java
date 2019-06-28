package org.ncr.nlidb.model;


import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="transaction_details")

public class TransactionDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int transactionId;
	private String transactionStatus;
	private String transactionResponseCode;
	private String transactionDate;
	private int transactionAmount;
	private int cardId;
	private int terminalId;

	
@ManyToOne
@JoinColumn(name="cardId",
              foreignKey = @ForeignKey(name = "FK_card_id"),insertable=false,updatable=false)
private CardDetails cardDetails;
@ManyToOne
@JoinColumn(name="terminalId",
              foreignKey = @ForeignKey(name = "FK_terminal_id"),insertable=false,updatable=false)
private AtmTerminal atmTerminal;



@Column(name="transaction_id")
public int getTransactionID() {
	return transactionId;
}

@Column(name="transaction_status",nullable=false)
public String getTransactionStatus() {
	return transactionStatus;
}
@Column(name="transaction_response_code",nullable=false)
public String getTransactionResponseCode() {
	return transactionResponseCode;
}
@Column(name="transaction_date",nullable=false)
public String getTransactionDate() {
	return transactionDate;
}
@Column(name="transaction_amount",nullable=false)
public int getTransactionAmount() {
	return transactionAmount;
}
@Column(name="card_id",nullable=false)
public int getCardID() {
	return cardId;
}
@Column(name="terminal_id",nullable=false)
public int getTerminalID() {
	return terminalId;
}

@Override
public String toString() {
	return "transactionDetails [TransactionID=" + transactionId + ", TransactionStatus=" + transactionStatus
			+ ", TransactionResponseCode=" + transactionResponseCode + ", TrasactionDateTime=" + transactionDate
			+ ", TransactionAmount=" + transactionAmount + ", CardID=" + cardId + ", TerminalID=" + terminalId + "]";
}	
	
	
}
