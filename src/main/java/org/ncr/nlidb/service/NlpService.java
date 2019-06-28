package org.ncr.nlidb.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.json.simple.JSONObject;
import org.ncr.nlidb.model.AccountDetails;
import org.ncr.nlidb.model.AtmTerminal;
import org.ncr.nlidb.model.BankDetails;
import org.ncr.nlidb.model.CardDetails;
import org.ncr.nlidb.model.Customer;
import org.ncr.nlidb.model.Product;
import org.ncr.nlidb.model.TableDictionary;
import org.ncr.nlidb.model.TransactionDetails;
import org.ncr.nlidb.repository.AccountDetailsRepository;
import org.ncr.nlidb.repository.AtmTerminalRepository;
import org.ncr.nlidb.repository.BankDetailsRepository;
import org.ncr.nlidb.repository.CardDetailsRepository;
import org.ncr.nlidb.repository.CustomerRepository;
import org.ncr.nlidb.repository.CustomerRepositoryImpl;
import org.ncr.nlidb.repository.DataDictionaryRepository;
import org.ncr.nlidb.repository.ProductRepository;
import org.ncr.nlidb.repository.TableDictionaryRepository;
import org.ncr.nlidb.repository.TransactionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.lemmatizer.*;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException; 



@Service
public class NlpService
{
	@Autowired
	private TableDictionaryRepository ddr;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired 
	private BankDetailsRepository bankRepository;
	@Autowired
	private AtmTerminalRepository atmRepository;
	@Autowired
	private AccountDetailsRepository accountRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CardDetailsRepository cardRepository;
	@Autowired
	private TransactionDetailsRepository transactionRepository;
	
	public String[] getTokens(String sentence) throws InvalidFormatException, IOException
	{     
	      WhitespaceTokenizer whitespaceTokenizer= WhitespaceTokenizer.INSTANCE; 
	      String[] tokens = whitespaceTokenizer.tokenize(sentence); 
	  
	      return tokens;
	}
	public String getPosTags(String query)
	{
		// Initialize the tagger
        MaxentTagger tagger = new MaxentTagger(
                "C:/Users/dheer/opennlp/english-left3words-distsim.tagger");
 
        // The sample string
       // String sample = "This is a sample text";
 
        // The tagged string
        String tagged = tagger.tagString(query);
 
        // Output the result
       // System.out.println(tagged);
        return tagged;
	}
	public String[] getChunks(String[] tokens,String[] tags) throws InvalidFormatException, IOException
	{
	      //Loading the chunker model 
	      InputStream inputStream = new 
	         FileInputStream("C:/Users/dheer/opennlp/en-chunker.bin"); 
	      ChunkerModel chunkerModel = new ChunkerModel(inputStream);  
	      
	      //Instantiate the ChunkerME class 
	      ChunkerME chunkerME = new ChunkerME(chunkerModel);
	       
	      //Generating the chunks 
	      String chunks[] = chunkerME.chunk(tokens, tags); 
	  
	   /*   for (String s : chunks) 
	         System.out.println(s);            
	   */   return chunks;
	}
	public List<String> getLemmas(String[] tokens,String[] tags) throws FileNotFoundException,ArrayIndexOutOfBoundsException
	{
		InputStream dictLemmatizer =null;
		
		dictLemmatizer = new FileInputStream("C:/Users/dheer/opennlp/en-lemmatizer.dict.txt");
	
	
		SimpleLemmatizer lem = new SimpleLemmatizer(dictLemmatizer);
		List<String> lemmas=new ArrayList<>();
		for(int i=0;i<tokens.length;i++)
		{
		//	System.out.println(tokens[i]+"     "+tags[i]);
			lemmas.add(lem.lemmatize(tokens[i], tags[i]));
		}
		return lemmas;
	} 
	
	
	

	@SuppressWarnings("unchecked")
	public String getResults(List<String>lemmas,List<String> tags) throws IOException
	{
		
		List<Customer> customers=new ArrayList<Customer>();
		List<BankDetails> bankDetails=new ArrayList<BankDetails>();
		List<AtmTerminal> atmDetails=new ArrayList<AtmTerminal>();
		List<AccountDetails> accountDetails=new ArrayList<AccountDetails>();
		List<Product> products=new ArrayList<>();
		List<CardDetails> cardDetails=new ArrayList<>();
		List<TransactionDetails> transactionDetails=new ArrayList<>();
		
		Gson gsonBuilder = new GsonBuilder().create();
	
		List<TableDictionary> tableDictionary=new ArrayList<>();

		ddr.findAll().forEach(tableDictionary::add);
		String tableName=null;
		int tagsIterator=0;
		int flag=0;
		String adjectiveCondition=null;
		String numberCondition=null;
	 	for(tagsIterator=0;tagsIterator<tags.size();tagsIterator++)
		{
			if(tags.get(tagsIterator).equals("NN")==true  || tags.get(tagsIterator).equals("NNS")==true
			||tags.get(tagsIterator).equals("NNPS")==true || tags.get(tagsIterator).equals("NNP")==true)
			{
				System.out.println("in tags"+tags.get(tagsIterator));
				for(int j=0;j<tableDictionary.size();j++)
			    { 
					if((lemmas.get(tagsIterator)).equals(tableDictionary.get(j).getTableSynonym())==true)
				    {
				       tableName=tableDictionary.get(j).getTableName();
				       if(tableName.equals("customer"))
				       {
				    	   customerRepository.findAll().forEach(customers::add);
				    	   System.out.println("in table matching");
				    	   if(lemmas.size()==1)
						   {
				    		   System.out.println("in size 1");
				    		   String jsonFromPojo = gsonBuilder.toJson(customers);
				    		  
				    		   return jsonFromPojo;
						   }
				    	   flag=1;
				       }
				       else if(tableName.equals("bank_branch_details"))
				       {
				    	   bankRepository.findAll().forEach(bankDetails::add);
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(bankDetails);
				    		 
				    		   return jsonFromPojo;
				    	   }
				    	   flag=1;
				       }
				       else if(tableName.equals("atm_terminal"))
				       {
				    	   atmRepository.findAll().forEach(atmDetails::add);
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(atmDetails);
				    	
				    		   return jsonFromPojo;
				    	   }
				    	   flag=1;
				       }
				       else if(tableName.equals("account_details"))
				       {
				    	   accountRepository.findAll().forEach(accountDetails::add);
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(accountDetails);
				    		  
				    		   return jsonFromPojo;
				    	   }
				    	   flag=1;
				       }
				       else if(tableName.equals("product"))
				       {
				    	   productRepository.findAll().forEach(products::add);
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(products);
				    		   
				    		   return jsonFromPojo;
				    	   }
				    	   flag=1;
				       }
				       else if(tableName.equals("card_details"))
				       {
				    	   cardRepository.findAll().forEach(cardDetails::add);
				    	   for(CardDetails c:cardDetails)
				    	   {
				    		   System.out.println(c);
				    	   }
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(cardDetails);
				    		  
				    		   return jsonFromPojo;
				    	   }
				    	   flag=1;
				       }
				       else if(tableName.equals("transaction_details"))
				       {
				    	   transactionRepository.findAll().forEach(transactionDetails::add);
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(transactionDetails);
				    		  
				    		   return jsonFromPojo;
				    	   }
				    	   flag=1;
				       }
				       else
				       {
				    	   
				    	   System.out.println("error 1");
				    	   String errorMessage="[{\"please enter the correct sentence\" :\"error 1-No Table found\"}]";
				    	  
				    	   return errorMessage;
				       }
				       break;
				    }
				 }
			}
			else if(tags.get(tagsIterator).equals("CD"))
			{
				numberCondition=lemmas.get(tagsIterator);
			}
			else if(tags.get(tagsIterator).equals("JJ")||tags.get(tagsIterator).equals("RB"))
			{
				adjectiveCondition=lemmas.get(tagsIterator);
			}
			if(flag==1)
			{
				System.out.println("flag is 1");
				break;
			}
	 	}
	 	if(flag==0)
	 	{
	 		
	 		System.out.println("error 2");
	 		String errorMessage="[{\"please enter the correct sentence\" :\"error 1-No Table found\"}]";
	    	  
	    	   return errorMessage;
	 	}
	 	tagsIterator++;
	 	List<String> columnAttributes=new ArrayList<>();
	 	List<String> columnValues=new ArrayList<>();
	 	List<String> conjunctions=new ArrayList<>();
	 	String columnValueMatch=null;
	 	flag=0;
	 	for(;tagsIterator<tags.size();tagsIterator++)
	 	{
	 		if(tags.get(tagsIterator).equals("NN")==true  || tags.get(tagsIterator).equals("NNS")==true
	 			   ||tags.get(tagsIterator).equals("NNPS")==true || tags.get(tagsIterator).equals("NNP")==true
	 			   ||tags.get(tagsIterator).equals("VBZ")||tags.get(tagsIterator).equals("VB")==true   
	 			   || tags.get(tagsIterator).equals("VBG")==true||tags.get(tagsIterator).equals("VBD")==true  
	 			   || tags.get(tagsIterator).equals("VBN")==true||tags.get(tagsIterator).equals("VBP")==true
	 			   ||tags.get(tagsIterator).equals("CD")||tags.get(tagsIterator).equals("RB"))
	 		{
	 			
	 			if(tableName.equals("customer"))
	 			{
	 				String columnName=CustomerColumns.isCustomerColumn(lemmas.get(tagsIterator));
	 				System.out.println(columnName+"    "+lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					System.out.println(flag+ " hello  hai "+columnName+"  "+lemmas.get(tagsIterator));
	 					if(flag==0)
	 					{
	 						if(columnAttributes.size()==0)
	 						{
	 							String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
						    	  
		 				    	return errorMessage;
	 						}
	 						else
	 						{
	 							columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 							columnValues.add(lemmas.get(tagsIterator));
	 						//	flag=1;
	 						}
	 					}
	 					else if(flag==1)
	 					{
	 						if(tags.get(tagsIterator).equals("VBZ"))
	 						{
	 							columnValueMatch=lemmas.get(tagsIterator);
	 						}
	 						else
	 						{
	 							columnValues.add(lemmas.get(tagsIterator));
	 							flag=0;
	 						}
	 					}
	 				}
	 				else
	 				{
	 					if(columnAttributes.size()!=columnValues.size())
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
					    	  
	 				    	return errorMessage;
	 					}
	 					columnAttributes.add(columnName);
		 				flag=1;

	 				}
	 			}
	 			else if(tableName.equals("bank_branch_details"))
	 			{
	 				String columnName=BranchColumns.isBranchColumn(lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					System.out.println(flag+ " hello  hai "+columnName+"  "+lemmas.get(tagsIterator));
	 					if(flag==0)
	 					{
	 						if(columnAttributes.size()==0)
	 						{
	 							String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
						    	  
		 				    	return errorMessage;
	 						}
	 						else
	 						{
	 							columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 							columnValues.add(lemmas.get(tagsIterator));
	 						//	flag=1;
	 						}
	 					}
	 					else if(flag==1)
	 					{
	 						if(tags.get(tagsIterator).equals("VBZ"))
	 						{
	 							columnValueMatch=lemmas.get(tagsIterator);
	 						}
	 						else
	 						{
	 							columnValues.add(lemmas.get(tagsIterator));
	 							flag=0;
	 						}
	 					}
	 				}
	 				else
	 				{
	 					if(columnAttributes.size()!=columnValues.size())
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
					    	  
	 				    	return errorMessage;
	 					}
	 					columnAttributes.add(columnName);
		 				flag=1;
	 				}
	 			}
	 			else if(tableName.equals("atm_terminal"))
	 			{
	 				String columnName=TerminalColumns.isTerminalColumn(lemmas.get(tagsIterator));
	 				System.out.println(columnName+" hello hai bye");
	 				if(columnName.equals("no_match_found"))
	 				{
	 					System.out.println(flag+ " hello  hai "+columnName+"  "+lemmas.get(tagsIterator));
	 					if(flag==0)
	 					{
	 						if(columnAttributes.size()==0)
	 						{
	 							String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
						    	  
		 				    	return errorMessage;
	 						}
	 						else
	 						{
	 							columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 							columnValues.add(lemmas.get(tagsIterator));
	 						//	flag=1;
	 						}
	 					}
	 					else if(flag==1)
	 					{
	 						if(tags.get(tagsIterator).equals("VBZ"))
	 						{
	 							columnValueMatch=lemmas.get(tagsIterator);
	 						}
	 						else
	 						{
	 							columnValues.add(lemmas.get(tagsIterator));
	 							flag=0;
	 						}
	 					}
	 				}
	 				else
	 				{
	 					if(columnAttributes.size()!=columnValues.size())
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
					    	  
	 				    	return errorMessage;
	 					}
	 					columnAttributes.add(columnName);
		 				flag=1;
	 				}
	 			}
	 			else if(tableName.equals("account_details"))
	 			{
	 				String columnName=AccountColumns.isAccountColumn(lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					System.out.println(flag+ " hello  hai "+columnName+"  "+lemmas.get(tagsIterator));
	 					if(flag==0)
	 					{
	 						if(columnAttributes.size()==0)
	 						{
	 							String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
						    	  
		 				    	return errorMessage;
	 						}
	 						else
	 						{
	 							columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 							columnValues.add(lemmas.get(tagsIterator));
	 						//	flag=1;
	 						}
	 					}
	 					else if(flag==1)
	 					{
	 						if(tags.get(tagsIterator).equals("VBZ"))
	 						{
	 							columnValueMatch=lemmas.get(tagsIterator);
	 						}
	 						else
	 						{
	 							columnValues.add(lemmas.get(tagsIterator));
	 							flag=0;
	 						}
	 					}
	 				}
	 				else
	 				{
	 					if(columnAttributes.size()!=columnValues.size())
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
					    	  
	 				    	return errorMessage;
	 					}
	 					columnAttributes.add(columnName);
		 				flag=1;
	 				}
	 			}
	 			else if(tableName.equals("card_details"))
	 			{
	 				String columnName=CardDetailsColumns.isCardDetailsColumn(lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					if(flag==0)
	 					{
	 						if(columnAttributes.size()==0)
	 						{
	 							String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
						    	  
		 				    	return errorMessage;
	 						}
	 						else
	 						{
	 							columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 							columnValues.add(lemmas.get(tagsIterator));
	 						//	flag=1;
	 						}
	 					}
	 					else if(flag==1)
	 					{
	 						if(tags.get(tagsIterator).equals("VBZ"))
	 						{
	 							columnValueMatch=lemmas.get(tagsIterator);
	 						}
	 						else
	 						{
	 							columnValues.add(lemmas.get(tagsIterator));
	 							flag=0;
	 						}
	 					}
	 					columnValues.add(lemmas.get(tagsIterator));
	 					flag=0;
	 				}
	 				else
	 				{
	 					if(columnAttributes.size()!=columnValues.size())
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
					    	  
	 				    	return errorMessage;
	 					}
	 					columnAttributes.add(columnName);
		 				flag=1;
	 				}
	 			}
	 			else if(tableName.equals("transaction_details"))
	 			{
	 				String columnName=TransactionDetailsColumns.isTransactionColumn(lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					if(flag==0)
	 					{
	 						if(columnAttributes.size()==0)
	 						{
	 							String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
						    	  
		 				    	return errorMessage;
	 						}
	 						else
	 						{
	 							columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 							columnValues.add(lemmas.get(tagsIterator));
	 						//	flag=1;
	 						}
	 					}
	 					else if(flag==1)
	 					{
	 						if(tags.get(tagsIterator).equals("VBZ"))
	 						{
	 							columnValueMatch=lemmas.get(tagsIterator);
	 						}
	 						else
	 						{
	 							columnValues.add(lemmas.get(tagsIterator));
	 							flag=0;
	 						}
	 					}
	 					columnValues.add(lemmas.get(tagsIterator));
	 					flag=0;
	 				}
	 				else
	 				{
	 					if(columnAttributes.size()!=columnValues.size())
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details\"}]";
					    	  
	 				    	return errorMessage;
	 					}
	 					columnAttributes.add(columnName);
		 				flag=1;
	 				}
	 			}
	 		}
	 		else if(tags.get(tagsIterator).equals("CC"))
	 		{
	 			conjunctions.add(lemmas.get(tagsIterator));
	 		}
	 		else
	 		{
	 			String errorMessage="[{\"please enter the correct sentence\" :\"error 2-Incorrect column details3\"}]";
		    	  
		    	   return errorMessage;
	 		
	 		}
	 	}
	 	System.out.println(columnAttributes.size()+"    "+columnValues.size()+"     "+conjunctions.size());
	 	if(columnAttributes.size()!=columnValues.size())
	 	{
	 		//String jsonFromPojo = gsonBuilder.toJson(customers);
 		  
	           System.out.println("error 4");
 		   String errorMessage="[{\"please enter the correct sentence\" :\"2-Incorrect column details4\"}]";
	    	  
    	   return errorMessage;
	 	}
	 	//System.out.println(columnAttributes.get(0)+" "+columnValues.get(0)+" "+columnAttributes.get(1)+" "+columnValues.get(1));
	 	if(columnAttributes.size()>0)
	 	{
	 		if(columnAttributes.size()==1)
	 		{
	 			System.out.println("hello dheeraj");
	 			if(tableName.equals("customer"))
	 			{
	 				if(columnValueMatch==null)
	 				{
	 					customers=CustomerColumns.filterOnSingleCondition(customers,columnAttributes.get(0),columnValues.get(0));
	 				}
	 				else
	 				{
	 					customers=customerRepository.likeMethod(columnAttributes.get(0),columnValues.get(0),columnValueMatch);
	 					if(customers.size()==0)
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error1 \"}]";
	 		 	    	    return errorMessage;
	 					}
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(customers);
		    		   
	 			//	String errorMessage="[{\"please enter the correct sentence\" :\"error \"}]";
	 		    	return jsonFromPojo;  
	 	    	  // return errorMessage;
	 				}
	 			}
	 			else if(tableName.equals("bank_branch_details"))
	 			{
	 				if(columnValueMatch==null)
	 				{
	 					bankDetails=BranchColumns.filterOnSingleCondition(bankDetails,columnAttributes.get(0),columnValues.get(0));
	 				}
	 				else
	 				{
	 					bankDetails=bankRepository.likeMethod(columnAttributes.get(0),columnValues.get(0),columnValueMatch);
	 					if(bankDetails.size()==0)
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error1 \"}]";
	 		 	    	    return errorMessage;
	 					}
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(bankDetails);
		    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("atm_terminal"))
	 			{
	 				if(columnValueMatch==null)
	 				{
	 					atmDetails=TerminalColumns.filterOnSingleCondition(atmDetails,columnAttributes.get(0),columnValues.get(0));
	 				}
	 				else
	 				{
	 					atmDetails=atmRepository.likeMethod(columnAttributes.get(0),columnValues.get(0),columnValueMatch);
	 					if(atmDetails.size()==0)
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error1 \"}]";
	 		 	    	    return errorMessage;
	 					}
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(atmDetails);
		    		   
		    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("account_details"))
	 			{
	 				if(columnValueMatch==null)
	 				{
	 					accountDetails=AccountColumns.filterOnSingleCondition(accountDetails,columnAttributes.get(0),columnValues.get(0));
	 				}
	 				else
	 				{
	 					accountDetails=accountRepository.likeMethod(columnAttributes.get(0),columnValues.get(0),columnValueMatch);
	 					if(atmDetails.size()==0)
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error1 \"}]";
	 		 	    	    return errorMessage;
	 					}
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(accountDetails);
		    		  
		    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("card_details"))
	 			{
	 				if(columnValueMatch==null)
	 				{
	 					cardDetails=CardDetailsColumns.filterOnSingleCondition(cardDetails,columnAttributes.get(0),columnValues.get(0));
	 				}
	 				else
	 				{
	 					cardDetails=cardRepository.likeMethod(columnAttributes.get(0),columnValues.get(0),columnValueMatch);
	 					if(cardDetails.size()==0)
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error1 \"}]";
	 		 	    	    return errorMessage;
	 					}
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(cardDetails);
		    		   
		    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("transaction_details"))
	 			{
	 				if(columnValueMatch==null)
	 				{
	 					transactionDetails=TransactionDetailsColumns.filterOnSingleCondition(transactionDetails,columnAttributes.get(0),columnValues.get(0));
	 				}
	 				else
	 				{
	 					transactionDetails=transactionRepository.likeMethod(columnAttributes.get(0),columnValues.get(0),columnValueMatch);
	 					if(transactionDetails.size()==0)
	 					{
	 						String errorMessage="[{\"please enter the correct sentence\" :\"error1 \"}]";
	 		 	    	    return errorMessage;
	 					}
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(cardDetails);
		    		 
		    		   return jsonFromPojo;
	 				}
	 			}
	 		}
	 		else
	 		{
	 			if(tableName.equals("customer"))
	 			{
	 				if(conjunctions.size()==0 || (conjunctions.size()+1)!=columnAttributes.size())
	 				{
	 					String errorMessage="[{\"please enter the correct sentence\" :\"error2\"}]";
	 			    	  
	 		    	   return errorMessage;
			    		
	 				}
	 				else
	 				{
	 					customers=customerRepository.findResultsByTwoConditions(columnAttributes.get(0), columnValues.get(0), conjunctions.get(0), columnAttributes.get(1), columnValues.get(1));
	 					String jsonFromPojo = gsonBuilder.toJson(customers);
			    		  
			    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("bank_branch_details"))
	 			{
	 				if(conjunctions.size()==0 || (conjunctions.size()+1)!=columnAttributes.size())
	 				{
	 					
	 					String errorMessage="[{\"please enter the correct sentence\" :\"error3\"}]";
	 			    	  
	 		    	   return errorMessage;
	 				}
	 				else
	 				{
	 					bankDetails=bankRepository.findResultsByTwoConditions(columnAttributes.get(0), columnValues.get(0), conjunctions.get(0), columnAttributes.get(1), columnValues.get(1));
	 					String jsonFromPojo = gsonBuilder.toJson(bankDetails);
			    		  
			    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("atm_terminal"))
	 			{
	 				if(conjunctions.size()==0 || (conjunctions.size()+1)!=columnAttributes.size())
	 				{
	 					
	 					String errorMessage="[{\"please enter the correct sentence\" :\"error4\"}]";
	 			    	  
	 		    	   return errorMessage;
	 				}
	 				else
	 				{
	 					atmDetails=atmRepository.findResultsByTwoConditions(columnAttributes.get(0), columnValues.get(0), conjunctions.get(0), columnAttributes.get(1), columnValues.get(1));
	 					String jsonFromPojo = gsonBuilder.toJson(atmDetails);
			    		  
			    		   return jsonFromPojo;
	 				}
	 			}
	 			else if(tableName.equals("account_details"))
	 			{
	 				if(conjunctions.size()==0 || (conjunctions.size()+1)!=columnAttributes.size())
	 				{
	 					
	 					String errorMessage="[{\"please enter the correct sentence\" :\"error5\"}]";
	 			    	  
	 		    	   return errorMessage;
	 				}
	 				else
	 				{
	 					accountDetails=accountRepository.findResultsByTwoConditions(columnAttributes.get(0), columnValues.get(0), conjunctions.get(0), columnAttributes.get(1), columnValues.get(1));
	 					String jsonFromPojo = gsonBuilder.toJson(atmDetails);
			    		   
			    		   return jsonFromPojo;
	 				}
	 			}
	 		}
	 	}
	 	/*else
	 	{
	 		fw.close();
	 		 //FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
  		   
	 		return "please enter the correct sentence :(";
	 	}*/
	 	if(numberCondition!=null||adjectiveCondition!=null)
	 	{
	 		if(tableName.equals("customer")) 
	 		{
	 			int number=1;
	 			if(numberCondition!=null)
	 			{
	 				number=Integer.valueOf(numberCondition)>customers.size()?customers.size():Integer.valueOf(numberCondition);
	 			}
	 			if(adjectiveCondition.equals("first"))
	 			{
	 				for(int i=number;i<customers.size();i++)
	 				{
	 					customers.remove(i);
	 					i--;
	 				}
	 			}
	 			if(adjectiveCondition.equals("last"))
	 			{
	 				for(int i=0;i<customers.size()-number;i++)
					{
	 					customers.remove(i);
	 					i--;
	 				}
	 			}
	 			String jsonFromPojo = gsonBuilder.toJson(customers);
	    		   
		       
	    		   return jsonFromPojo;
	 		}
	 		else if(tableName.equals("bank_branch_details"))
	 		{
	 			int number=1;
	 			if(numberCondition!=null)
	 			{
	 				number=Integer.valueOf(numberCondition)>bankDetails.size()?bankDetails.size():Integer.valueOf(numberCondition);
	 			}
	 			if(adjectiveCondition.equals("first"))
	 			{
	 				for(int i=number;i<bankDetails.size();i++)
	 				{
	 					bankDetails.remove(i);
	 					i--;
	 				}
	 			}
	 			if(adjectiveCondition.equals("last"))
	 			{
	 				for(int i=0;i<bankDetails.size()-number;i++)
					{
	 					bankDetails.remove(i);
	 					i--;
	 				}
	 			}
	 			String jsonFromPojo = gsonBuilder.toJson(bankDetails);
	    		
	    		   return jsonFromPojo;
	 		}
	 		else if(tableName.equals("atm_terminal"))
	 		{
	 			int number=1;
	 			if(numberCondition!=null)
	 			{
	 				number=Integer.valueOf(numberCondition)>atmDetails.size()?atmDetails.size():Integer.valueOf(numberCondition);
	 			}
	 			if(adjectiveCondition.equals("first"))
	 			{
	 				for(int i=number;i<atmDetails.size();i++)
	 				{
	 					atmDetails.remove(i);
	 					i--;
	 				}
	 			}
	 			if(adjectiveCondition.equals("last"))
	 			{
	 				for(int i=0;i<atmDetails.size()-number;i++)
					{
	 					atmDetails.remove(i);
	 					i--;
	 				}
	 			}
	 			String jsonFromPojo = gsonBuilder.toJson(atmDetails);
	    		  
	    		   return jsonFromPojo;
	 		}
	 		else if(tableName.equals("account_details"))
	 		{
	 			int number=1;
	 			if(numberCondition!=null)
	 			{
	 				number=Integer.valueOf(numberCondition)>accountDetails.size()?accountDetails.size():Integer.valueOf(numberCondition);
	 			}
	 			if(adjectiveCondition.equals("first"))
	 			{
	 				for(int i=number;i<accountDetails.size();i++)
	 				{
	 					accountDetails.remove(i);
	 					i--;
	 				}
	 			}
	 			if(adjectiveCondition.equals("last"))
	 			{
	 				for(int i=0;i<accountDetails.size()-number;i++)
					{
	 					accountDetails.remove(i);
	 					i--;
	 				}
	 			}
	 			String jsonFromPojo = gsonBuilder.toJson(accountDetails);
	    		  
	    		   return jsonFromPojo;
	 		}
	 		
	 	}
	 
	 	
	 	String errorMessage="[{\"please enter the correct sentence\" :\"error6\"}]";
  	  
 	   return errorMessage;
	}
	
}