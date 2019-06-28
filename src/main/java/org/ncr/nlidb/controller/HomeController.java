package org.ncr.nlidb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ncr.nlidb.model.AccountDetails;
import org.ncr.nlidb.model.Customer;
import org.ncr.nlidb.model.DataDictionary;
import org.ncr.nlidb.repository.CustomerRepository;
import org.ncr.nlidb.service.NlpService;
import org.ncr.nlidb.service.StopWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import opennlp.tools.util.InvalidFormatException;

@Controller
public class HomeController 
{
	@Autowired
	private NlpService serviceProvider;
	@Autowired
	private StopWord stopWord;
	
	@RequestMapping(value="/submitQuery",method=RequestMethod.POST)
	public String obtainResults(@RequestParam("name")String name,Model model) throws InvalidFormatException, IOException
	{
		
		//model.setViewName("querySubmit.jsp");
		//for tokenisation of the input string
		String[] tokens=serviceProvider.getTokens(name);
		
		//for obtaining the POS tags to the tokens in the string
		String tagged=serviceProvider.getPosTags(name);
		List<String> tags=new ArrayList<>(); 
		for(int i=1;i<tagged.length();i++)
		{
			if(tagged.charAt(i-1)=='_')
			{
				String tag="";
				while(i<tagged.length() && tagged.charAt(i)!=' ')
				{
					char c=tagged.charAt(i);
					tag+=Character.toString(c);
					i++;
				}
				tags.add(tag);
			}
		}
		String []tempTags=new String[tags.size()];
		int ind=0;
		for(String s:tags)
		{
			tempTags[ind++]=s;
		}
		List<String> lemmas=serviceProvider.getLemmas(tokens, tempTags);
		for(int i=0;i<lemmas.size();i++)
		{
			System.out.println("hello "+lemmas.get(i)+" "+tokens[i]+" "+ tags.get(i));
		}
		for(int i=0;i<lemmas.size();i++)
		{
			if(stopWord.isStopWord(lemmas.get(i))==true)
			{
				System.out.println(lemmas.get(i)+"    "+tags.get(i));
				lemmas.remove(lemmas.get(i));
				tags.remove(tags.get(i));
				i--;
			}
		}
		for(int i=0;i<lemmas.size();i++)
		{
			System.out.println(lemmas.get(i)+" "+tags.get(i));
		}
		String jsonContent= serviceProvider.getResults(lemmas, tags);
		model.addAttribute("jsonContent",jsonContent);
		return "index";
		
		
	}
}
