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

import org.ncr.nlidb.model.AccountDetails;
import org.ncr.nlidb.model.AtmTerminal;
import org.ncr.nlidb.model.BankDetails;
import org.ncr.nlidb.model.Customer;

import org.ncr.nlidb.model.TableDictionary;
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
	private ProductRepository productRepository;
	private CardDetailsRepository cardRepository;
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
	
	
	

	public String getResults(List<String>lemmas,List<String> tags) throws IOException
	{
		
		List<Customer> customers=new ArrayList<Customer>();
		List<BankDetails> bankDetails=new ArrayList<BankDetails>();
		List<AtmTerminal> atmDetails=new ArrayList<AtmTerminal>();
		List<AccountDetails> accountDetails=new ArrayList<AccountDetails>();
		
		
		Gson gsonBuilder = new GsonBuilder().create();
		FileWriter fw=new FileWriter("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json"); 
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
				    		   fw.write(jsonFromPojo);   
				    		   fw.flush();
				    		   fw.close();
				    		   //FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
				    		  
				    		   return "viewTable.html";
						   }
				    	   flag=1;
				       }
				       else if(tableName.equals("bank_branch_details"))
				       {
				    	   bankRepository.findAll().forEach(bankDetails::add);
				    	   if(lemmas.size()==1)
				    	   {
				    		   String jsonFromPojo = gsonBuilder.toJson(bankDetails);
				    		   fw.write(jsonFromPojo);   
				    		   fw.flush();
					           fw.close();  
				    		   return "viewTable.html";
				    	   }
				    	   flag=1;
				       }
				       else if(tableName.equals("atm_terminal"))
				       {
				    	   atmRepository.findAll().forEach(atmDetails::add);
				    	   flag=1;
				       }
				       else if(tableName.equals("account_details"))
				       {
				    	   accountRepository.findAll().forEach(accountDetails::add);
				    	   flag=1;
				       }
				       else if(tableName.equals("product"))
				       {
				    	   customerRepository.findAll().forEach(customers::add);
				    	   flag=1;
				       }
				       else if(tableName.equals("card_details"))
				       {
				    	   bankRepository.findAll().forEach(bankDetails::add);
				    	   flag=1;
				       }
				       else if(tableName.equals("transaction_details"))
				       {
				    	   customerRepository.findAll().forEach(customers::add);
				    	   flag=1;
				       }
				       else
				       {
				    	   fw.flush();
				    	   fw.close();
				    	  // FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
			    		   //fr.close();
				    	   return "please enter the correct sentence :(";
				       }
				       break;
				    }
				 }
			}
			else if(tags.get(tagsIterator).equals("CD"))
			{
				numberCondition=lemmas.get(tagsIterator);
			}
			else if(tags.get(tagsIterator).equals("JJ"))
			{
				adjectiveCondition=lemmas.get(tagsIterator);
			}
			if(flag==1)
			{
				System.out.println("flag is 1");
				break;
			}
	 	}
	 	tagsIterator++;
	 	List<String> columnAttributes=new ArrayList<>();
	 	List<String> columnValues=new ArrayList<>();
	 	List<String> conjunctions=new ArrayList<>();
	 	flag=0;
	 	for(;tagsIterator<tags.size();tagsIterator++)
	 	{
	 		if(tags.get(tagsIterator).equals("NN")==true  || tags.get(tagsIterator).equals("NNS")==true
	 			   ||tags.get(tagsIterator).equals("NNPS")==true || tags.get(tagsIterator).equals("NNP")==true
	 			   ||tags.get(tagsIterator).equals("VB")==true   || tags.get(tagsIterator).equals("VBG")==true
	 			   ||tags.get(tagsIterator).equals("VBD")==true  || tags.get(tagsIterator).equals("VBN")==true
	 			   ||tags.get(tagsIterator).equals("VBP")==true||tags.get(tagsIterator).equals("CD"))
	 		{
	 			
	 			if(tableName.equals("customer"))
	 			{
	 				String columnName=CustomerColumns.isCustomerColumn(lemmas.get(tagsIterator));
	 				System.out.println(columnName+"    "+lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					if(flag==0)
	 					{
	 						columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 					}
	 					columnValues.add(lemmas.get(tagsIterator));
	 					flag=0;
	 				}
	 				else
	 				{
	 					columnAttributes.add(columnName);
		 				flag=1;

	 				}
	 			}
	 			else if(tableName.equals("bank_branch_details"))
	 			{
	 				String columnName=BranchColumns.isBranchColumn(lemmas.get(tagsIterator));
	 				if(columnName.equals("no_match_found"))
	 				{
	 					if(flag==0)
	 					{
	 						columnAttributes.add(columnAttributes.get(columnAttributes.size()-1));
	 					}
	 					columnValues.add(lemmas.get(tagsIterator));
	 					flag=0;
	 				}
	 				else
	 				{
	 					columnAttributes.add(columnName);
		 				flag=1;
	 				}
	 			}
	 		}
	 		else if(tags.get(tagsIterator).equals("CC"))
	 		{
	 			conjunctions.add(lemmas.get(tagsIterator));
	 		}
	 	}
	 	System.out.println(columnAttributes.size()+"    "+columnValues.size()+"     "+conjunctions.size());
	 	if(columnAttributes.size()!=columnValues.size())
	 	{
	 		String jsonFromPojo = gsonBuilder.toJson(customers);
 		   fw.write(jsonFromPojo);    
 		   fw.flush();
	           fw.close();  
	         
 		   return "Please enter the correct sentence :(";
	 	}
	 	//System.out.println(columnAttributes.get(0)+" "+columnValues.get(0)+" "+columnAttributes.get(1)+" "+columnValues.get(1));
	 	if(columnAttributes.size()>0)
	 	{
	 		if(columnAttributes.size()==1)
	 		{
	 			System.out.println("hello dheeraj");
	 			if(tableName.equals("customer"))
	 			{
	 				System.out.println("hello inner loop");
	 				customers=CustomerColumns.filterOnSingleCondition(customers,columnAttributes.get(0),columnValues.get(0));
	 				//customers=customerRepository.findResultsBySingleCondition(columnAttributes.get(0), columnValues.get(0));
	 				for(Customer c:customers)
	 				{
	 					System.out.println(c);
	 				}
	 				if(adjectiveCondition==null && numberCondition==null)
	 				{
	 				String jsonFromPojo = gsonBuilder.toJson(customers);
		    		   fw.write(jsonFromPojo);    
		    		   fw.flush();
			           fw.close();  
			          // FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
		    		  
		    		   return "viewTable.html";
	 				}
	 			}
	 		}
	 		else
	 		{
	 			if(tableName.equals("customer"))
	 			{
	 				if(conjunctions.size()==0 || (conjunctions.size()+1)!=columnAttributes.size())
	 				{
	 					fw.flush();
	 					fw.close();
	 				//	 FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
			    		//   fr.close();
			    		return "Please enter the correct sentence :(";
	 				}
	 				else
	 				{
	 					customers=customerRepository.findResultsByTwoConditions(columnAttributes.get(0), columnValues.get(0), conjunctions.get(0), columnAttributes.get(1), columnValues.get(1));
	 					String jsonFromPojo = gsonBuilder.toJson(customers);
			    		   fw.write(jsonFromPojo);    
			    		   fw.flush();
				           fw.close();  
				           //FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
			    	
			    		   return "viewTable.html";
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
	    		   fw.write(jsonFromPojo);    
	    		   fw.flush();
		           fw.close();  
		          // FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
	    		  // fr.close();
		       
	    		   return "viewTable.html";
	 		}
	 	}
	 	// FileReader fr=new FileReader("C:\\Users\\dheer\\Desktop\\nlidb\\nlidb\\src\\main\\resources\\static\\project.json");
		  
	 	//f.close();
	 	
	 	fw.close();
	 
	 	return "Please enter the correct sentence :(";
	}
	
}