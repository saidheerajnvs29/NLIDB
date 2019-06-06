package org.ncr.nlidb.service;

import java.util.ArrayList;
import java.util.List;

import org.ncr.nlidb.model.AccountDetails;
import org.ncr.nlidb.model.Customer;
import org.ncr.nlidb.repository.AccountDetailsRepository;
import org.ncr.nlidb.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import opennlp.tools.tokenize.SimpleTokenizer; 

@Service
public class NlpService 
{
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private AccountDetailsRepository accountRepository;
	
	public List<AccountDetails> getDetails()
	{
		List<AccountDetails> accountRepo=new ArrayList<>();
		accountRepository.findAll()
		.forEach(accountRepo::add);
		return accountRepo;
		/*String sentence = "Hi. How are you? Welcome to Tutorialspoint. " 
		         + "We provide free tutorials on various technologies"; 
		    
		      //Instantiating SimpleTokenizer class 
		      SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;  
		       List<String> result=new ArrayList<>();
		      //Tokenizing the given sentence 
		      String[] tokens= simpleTokenizer.tokenize(sentence);  
		  
		      //Printing the tokens 
		      for(String token : tokens) {         
		         result.add(token);
		      }
		  return result;*/
		//return employeeRepository.findAll();
		
	}

}
