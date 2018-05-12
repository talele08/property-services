package org.egov.pt.repository.rowmapper;







import com.sun.prism.Texture;
import org.egov.pt.web.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

import java.security.acl.Owner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class PropertyRowMapper implements ResultSetExtractor<List<Property>> {


    @Override
    public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, Property> propertyMap = new TreeMap<>();

         try{

             while(rs.next()){

                 String propertId = rs.getString("id");

                 Property currentProperty= propertyMap.get(propertId);

                 if (currentProperty==null) {

                     AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
                             .createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastModifiedBy"))
                             .lastModifiedTime(rs.getLong("lastModifiedTime")).build();

                     Address address = Address.builder().id(rs.getString("id"))
                             .latitude(rs.getDouble("latitude")).longitude(rs.getDouble("longitude"))
                             .addressNumber(rs.getString("addressNumber")).type(rs.getString("type"))
                             .addressLine1(rs.getString("addressLine1")).addressLine2(rs.getString("addressLine2"))
                             .landmark(rs.getString("landmark")).city(rs.getString("city"))
                             .pincode(rs.getString("pincode")).detail(rs.getString("detail"))
                             .buildingName(rs.getString("buildingName")).street(rs.getString("street"))
                             .locality(Boundary.builder().code(rs.getString("locality")).build())
                             .build();

                     PropertyDetail propertDetail=PropertyDetail.builder()
                             .id(rs.getString("id"))
                             .source(PropertyDetail.SourceEnum.fromValue(rs.getString("source")))
                             .usage(rs.getString("usage")).noOfFloors(rs.getLong("nooffloors"))
                             .landArea(rs.getFloat("landarea")).buildUpArea(rs.getFloat("builduparea"))
                             .channel(PropertyDetail.ChannelEnum.fromValue(rs.getString("channel")))
                             .additionalDetails(rs.getString("additionaldetails"))
                             .build();

                 }

                 List<OwnerInfo> ownerInfos = currentProperty.getOwners();

                 if (CollectionUtils.isEmpty(ownerInfos)) {
                     ownerInfos = new ArrayList<>();
                     currentProperty.setOwners(ownerInfos);
                 }

                 OwnerInfo ownerInfo = OwnerInfo.builder().id(rs.getString("userid")).tenantId(rs.getString("tenantId")).build();
                 if (!ownerInfos.contains(ownerInfo))
                     ownerInfos.add(ownerInfo);



                 List<Document> documents=currentProperty.getPropertyDetail().getDocuments();

                 if (CollectionUtils.isEmpty(documents)) {
                     documents = new ArrayList<>();
                     currentProperty.getPropertyDetail().setDocuments((documents);
                 }

                 Document doc = Document.builder().id(rs.getString("documentid")).fileStore(rs.getString("filestore"))
                         .build();
                 if (!documents.contains(doc))
                     documents.add(doc);


                 List<Unit> units=currentProperty.getPropertyDetail().getUnits();

                 if(CollectionUtils.isEmpty(units)){
                     units=new ArrayList<>();
                     currentProperty.getPropertyDetail().setUnits(units);
                 }

                 Unit unit=Unit.builder().id(rs.getString("id")).floorNo(rs.getString("floorno"))
                         .unitType(rs.getString("unittype")).unitArea(rs.getFloat("unitarea"))
                         .build();
                 if(!units.contains(unit))
                     units.add(unit);


               List<List<UnitUsage>> usage  =  new  new ArrayList<ArrayList<UnitUsage>>();


             }



         }
         catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }





        return new ArrayList<>(propertyMap.values());

    }




    }
