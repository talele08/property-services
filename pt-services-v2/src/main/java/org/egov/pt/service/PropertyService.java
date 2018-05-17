package org.egov.pt.service;

import java.util.*;
import java.util.function.Predicate;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.producer.Producer;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.web.models.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
		List<String> responseids = new ArrayList<>();
		List<String> requestids = new ArrayList<>();

		request.getProperties().forEach(property -> {
			requestids.add(property.getId());
		});

		response.getProperties().forEach(property -> {
			responseids.add(property.getId());
		});

        boolean ifPropertyExists=listEqualsIgnoreOrder(responseids,requestids);

        if(ifPropertyExists) {
			enrichUpdateRequest(request,response);
			producer.push(config.getUpdatePropertyTopic(), request);
			return PropertyResponse.builder().properties(request.getProperties())
					.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.build();
		}
		else
		{    throw new CustomException("usr_002","invalid id");  // Change the error code

		}
	}


	public void enrichUpdateRequest(PropertyRequest request,PropertyResponse response) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails AuditDetails = propertyuutil.getAuditDetails(requestInfo.getUserInfo().getId().toString(), false);

		Map<String,Property> idToProperty = new HashMap<>();
        List<Property> propertiesFromResponse = response.getProperties();
        propertiesFromResponse.forEach(propertyFromResponse -> {
			idToProperty.put(propertyFromResponse.getId(),propertyFromResponse);
		});

		for (Property property : request.getProperties()){
			property.setAuditDetails(AuditDetails);
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
        Set<String> propertyDetailids = new HashSet<>();
        Set<String> unitids = new HashSet<>();
        Set<String> usageids = new HashSet<>();
        Set<String> documentids = new HashSet<>();
        Set<String> ownerids = new HashSet<>();
        Set<String> addressids = new HashSet<>();

		PropertyCriteria propertyCriteria = new PropertyCriteria();

		properties.forEach(property -> {
                 ids.add(property.getId());
                 if(!CollectionUtils.isEmpty(ids)) {
                     if(property.getPropertyDetail().getId()!=null)
                      propertyDetailids.add(property.getPropertyDetail().getId());
                     if(property.getAddress().getId()!=null)
                      addressids.add(property.getAddress().getId());
                     if(!CollectionUtils.isEmpty(property.getOwners()))
                        ownerids.addAll(getOwnerids(property));
                     if(!CollectionUtils.isEmpty(property.getPropertyDetail().getDocuments()))
                        documentids.addAll(getDocumentids(property.getPropertyDetail()));
                     if(!CollectionUtils.isEmpty(property.getPropertyDetail().getUnits())) {
                        unitids.addAll(getUnitids(property.getPropertyDetail()));
                      if(usageNotEmpty(property.getPropertyDetail().getUnits()))
                        usageids.addAll(getUsageids(property.getPropertyDetail()));
                     }
                 }
				}
		);

		propertyCriteria.setTenantId(properties.get(0).getTenantId());
		propertyCriteria.setIds(ids);
		propertyCriteria.setPropertyDetailids(propertyDetailids);
        propertyCriteria.setAddressids(addressids);
        propertyCriteria.setOwnerids(ownerids);
        propertyCriteria.setUnitids(unitids);
        propertyCriteria.setUsageids(usageids);
        propertyCriteria.setDocumentids(documentids);

		response = searchProperty(propertyCriteria);
		return response;
	}


    public Set<String> getOwnerids(Property property){
        Set<OwnerInfo> owners= property.getOwners();
        Set<String> ownerIds = new HashSet<>();
        owners.forEach(owner -> {
            if(owner.getId()!=null)
             ownerIds.add(owner.getId());
        });
        return ownerIds;
    }

    public Set<String> getUnitids(PropertyDetail propertyDetail){
	    Set<Unit> units= propertyDetail.getUnits();
	    Set<String> unitIds = new HashSet<>();
	    units.forEach(unit -> {
	        if(unit.getId()!=null)
	         unitIds.add(unit.getId());
        });
	    return unitIds;
    }

    public Set<String> getUsageids(PropertyDetail propertyDetail){
        Set<Unit> units= propertyDetail.getUnits();
        Set<UnitUsage> usages = new HashSet<>();
        Set<String> usageids = new HashSet<>();
        units.forEach(unit -> {
            usages.addAll(unit.getUsage());
        });
        usages.forEach(usage -> {
            if(usage.getId()!=null)
             usageids.add(usage.getId());
        });
        return usageids;
    }

    public Set<String> getDocumentids(PropertyDetail propertyDetail){
        Set<Document> documents= propertyDetail.getDocuments();
        Set<String> documentIds = new HashSet<>();
        documents.forEach(document -> {
            documentIds.add(document.getId());
        });
        return documentIds;
    }

    public boolean usageNotEmpty(Set<Unit> units){
        Predicate<Unit> p = unit -> !CollectionUtils.isEmpty(unit.getUsage()) ;
	    return units.stream().anyMatch(p);
    }

	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}




	}