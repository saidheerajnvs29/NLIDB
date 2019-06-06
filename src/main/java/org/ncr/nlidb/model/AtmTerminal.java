package org.ncr.nlidb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "atm_terminal")
public class AtmTerminal {
	
	@Id
	private int terminalId;
	private String terminalLocation;
	
	@Column(name="terminal_id")
	public int getTerminalId() {
		return terminalId;
	}
	
	@Column(name="terminal_location",nullable=false,unique=true)
	public String getTerminalLocation() {
		return terminalLocation;
	}

	@Override
	public String toString() {
		return "AtmTerminal [terminalId=" + terminalId + ", terminalLocation=" + terminalLocation + "]";
	}
	

}