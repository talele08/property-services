package org.egov.pt.service;

import java.util.*;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.producer.Producer;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

	@Autowired
	private Producer producer;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	PropertyUtil propertyuutil;

	public PropertyResponse createProperty(PropertyRequest request) {
		enrichCreateRequest(request);

		producer.push(config.getSavePropertyTopic(), request);
		return PropertyResponse.builder().properties(request.getProperties())
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
				.build();

	}

	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = propertyuutil.getAuditDetails(requestInfo.getUserInfo().getId().toString(), true);

		for (Property property : request.getProperties()) {
			property.setId(UUID.randomUUID().toString());
			property.getAddress().setId(UUID.randomUUID().toString());
			property.setAuditDetails(auditDetails);
			property.getPropertyDetail().setId(UUID.randomUUID().toString());
			Set<OwnerInfo> owners = property.getOwners();
			owners.forEach(owner -> {
				owner.setId(UUID.randomUUID().toString());
			});
			Set<Unit> units = property.getPropertyDetail().getUnits();
			units.forEach(unit -> {
				unit.setId(UUID.randomUUID().toString());
				unit.getUsage().forEach(usage -> usage.setId(UUID.randomUUID().toString()));
			});
			Set<Document> documents = property.getPropertyDetail().getDocuments();
			documents.forEach(document ->{
				document.setId(UUID.randomUUID().toString());
			});

		}
	}

	public PropertyResponse searchProperty(PropertyCriteria criteria) {

		List<Property> properties = repository.getProperties(criteria);
		return PropertyResponse.builder().properties(properties).responseInfo(new ResponseInfo()).build();
	}


	public PropertyResponse updateProperty(PropertyRequest request) {
		PropertyResponse response = propertyExists(request);
        boolean ifPropertyExists=listEqualsIgnoreOrder(response.getProperties(),request.getProperties());

        if(ifPropertyExists) {
			enrichUpdateRequest(request,response);
			producer.push(config.getUpdatePropertyTopic(), request);
			return PropertyResponse.builder().properties(request.getProperties())
					.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.build();
		}
		else
		{   System.out.println("update failed");
			return PropertyResponse.builder().properties(request.getProperties())
					.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), false))
					.build();

		}
	}


	public void enrichUpdateRequest(PropertyRequest request,PropertyResponse response) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = propertyuutil.getAuditDetails(requestInfo.getUserInfo().getId().toString(), false);
        Map<String,Property> idToProperty = new HashMap<>();
        List<Property> propertiesFromResponse = response.getProperties();
        propertiesFromResponse.forEach(propertyFromResponse -> {
			idToProperty.put(propertyFromResponse.getId(),propertyFromResponse);
		});

		for (Property property : request.getProperties()){
			property.setAuditDetails(auditDetails);
			String id = property.getId();
			Property responseProperty = idToProperty.get(id);

			property.getPropertyDetail().setId(responseProperty.getPropertyDetail().getId());
			property.getAddress().setId(responseProperty.getAddress().getId());


			Set<OwnerInfo> ownerInfos = property.getOwners();
			Set<Document> documents = property.getPropertyDetail().getDocuments();
			Set<Unit> units=property.getPropertyDetail().getUnits();

			if(ownerInfos!=null && !ownerInfos.isEmpty()) {
				ownerInfos.forEach(owner -> {
					if (owner.getId() == null) {
						owner.setId(UUID.randomUUID().toString());
					}
				});
			}

			if(documents!=null && !documents.isEmpty()){
		     	documents.forEach(document ->{
					if(document.getId()==null){
						document.setId(UUID.randomUUID().toString());
					}
				  });
			 }

			if(units!=null && !units.isEmpty()){
				units.forEach(unit ->{
					if(unit.getId()==null){
						unit.setId(UUID.randomUUID().toString());
					}
					Set<UnitUsage> usages = unit.getUsage();
					if(usages!=null && !usages.isEmpty()){
						usages.forEach(usage ->{
							if(usage.getId()==null){
								usage.setId(UUID.randomUUID().toString());
							}
						});
					}
				});
			}

		 }
	}


    public PropertyResponse propertyExists(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		PropertyResponse response=null;
		List<Property> properties=request.getProperties();
		Set<String> ids = new HashSet<>();
		PropertyCriteria propertyCriteria = new PropertyCriteria();

		properties.forEach(property -> {
                 ids.add(property.getId());
				}
		);

		propertyCriteria.setTenantId(properties.get(0).getTenantId());
		propertyCriteria.setIds(ids);

		response = searchProperty(propertyCriteria);
		return response;
	}







	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}



	}