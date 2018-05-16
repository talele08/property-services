package org.egov.pt.web.controllers;

import javax.validation.Valid;

import org.egov.pt.service.PropertyService;
import org.egov.pt.web.models.PropertyCriteria;
import org.egov.pt.web.models.PropertyRequest;
import org.egov.pt.web.models.PropertyResponse;
import org.egov.pt.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/property")
public class PropertyController {


@Autowired
private PropertyService propertyService;




	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest propertyRequest) {
		
		PropertyResponse response = null;
		response = propertyService.createProperty(propertyRequest);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<PropertyResponse> update(@Valid @RequestBody PropertyRequest propertyRequest) {
		
		PropertyResponse response = null;
		response=propertyService.updateProperty(propertyRequest);
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<PropertyResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute  PropertyCriteria propertyCriteria) {
		
		PropertyResponse response = null;
		response=propertyService.searchProperty(propertyCriteria);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
