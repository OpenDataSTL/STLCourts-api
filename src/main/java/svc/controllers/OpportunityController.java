package svc.controllers;

import javax.inject.Inject;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import svc.logging.LogSystem;
import svc.managers.*;
import svc.models.*;


@RestController
@EnableAutoConfiguration
@RequestMapping("inveo-api/opportunities")
public class OpportunityController
{	
	@Inject
	OpportunityManager _opportunityManager;
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	Court GetOpportunitiesForSponsor(@PathVariable("id") Integer id)
	{
		if (id == null)
		{
			LogSystem.LogEvent("Null id passed to controller");
		}
		
		return null;
	}
}