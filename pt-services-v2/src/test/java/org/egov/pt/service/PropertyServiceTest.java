package org.egov.pt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.util.FileUtil;
import org.egov.pt.util.FileUtils;
import org.egov.pt.web.models.AuditDetails;
import org.egov.pt.web.models.Property;
import org.egov.pt.web.models.PropertyRequest;

import java.io.IOException;

import static org.egov.pt.web.models.Property.StatusEnum.ACTIVE;

public class PropertyServiceTest {


    public void testcreateProperty(){


    }



    private void testenrichCreateRequest(){
        AuditDetails auditDetails = getAuditDetails();




    }





    private AuditDetails getAuditDetails(){
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy("xyz");
        auditDetails.setLastModifiedBy("xyz");
        auditDetails.setCreatedTime((long)10000);
        auditDetails.setLastModifiedTime((long)10000);

        return auditDetails;
    }



    private PropertyRequest getPropertRequest(final String filepath) throws IOException {
        final String propertRequestJson = new FileUtils().getFileContents(filepath);
        return new ObjectMapper().readValue(propertRequestJson, PropertyRequest.class);
    }





}
