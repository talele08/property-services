package org.egov.pt.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.pt.producer.Producer;
import org.egov.pt.util.ResponseInfoFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    @Autowired
    private Producer PropertyProducer;

    @Autowired
    private PropertyConfiguration mainConfiguration;


    @Autowired
    private ResponseInfoFactory responseInfoFactory;


    @Autowired
    PropertyUtil propertyuutil;

    public PropertyResponse createProperty(PropertyRequest request)
    {
        enrichCreateRequest(request);
        System.out.println("property object: "+request);

        PropertyProducer.push(mainConfiguration.getSavePropertyTopic(),request);
        return PropertyResponse.builder().properties(request.getProperties())
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();

    }


    public void enrichCreateRequest(PropertyRequest request) {
        // (1)
        RequestInfo requestInfo = request.getRequestInfo();
        System.err.println("requestingo: "+requestInfo);
        AuditDetails auditDetails = propertyuutil.getAuditDetails(requestInfo.getUserInfo().getId().toString(), true);

        for(Property property: request.getProperties()) {
            property.setId(UUID.randomUUID().toString());
            property.getAddress().setId(UUID.randomUUID().toString());
            property.setAuditDetails(auditDetails);
            property.getPropertyDetail().setId(UUID.randomUUID().toString());
            List<Unit> units=property.getPropertyDetail().getUnits();
            units.forEach(unit -> {
                unit.setId(UUID.randomUUID().toString());
                unit.getUsage().forEach(usage -> {
                    usage.setId(UUID.randomUUID().toString());
                });
            });


        }
    }






}
