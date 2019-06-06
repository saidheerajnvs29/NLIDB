package org.ncr.nlidb.controller;

import java.util.ArrayList;
import java.util.List;

import org.ncr.nlidb.model.AccountDetails;
import org.ncr.nlidb.model.Customer;
import org.ncr.nlidb.repository.CustomerRepository;
import org.ncr.nlidb.service.NlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController 
{
	@Autowired
	private NlpService serviceProvider;
	@Autowired
	private CustomerRepository customerRepository;
	
	@RequestMapping(value="/submitQuery",method=RequestMethod.GET)
	public Model obtainResults(@RequestParam("query")String query,Model model)
	{
		return model.addAttribute("message",query);
		
		//return serviceProvider.getDetails();
	}
}
