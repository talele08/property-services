package org.egov.pt.repository.builder;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.egov.pt.web.models.PropertyCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PropertyQueryBuilder {

	public static final String INNER_JOIN_STRING = "INNER JOIN";
	
	static final String QUERY = "SELECT pt.*,ptdl.*,address.*,owner.*,doc.*,unit.*,"
			+ " pt.id as propertyid,ptdl.id as propertydetailid,doc.id as documentid,unit.id as unitid,"
			+ "address.id as addresskeyid"
			+ " FROM eg_pt_property_v2 pt "
			+ INNER_JOIN_STRING
			+ " eg_pt_propertydetail_v2 ptdl ON pt.id=ptdl.property "
			+ INNER_JOIN_STRING
			+ " eg_pt_owner_v2 owner ON pt.id=owner.property "
			+ INNER_JOIN_STRING
			+ " eg_pt_document_v2 doc ON ptdl.id=doc.propertydetail "
			+ INNER_JOIN_STRING
			+ " eg_pt_unit_v2 unit ON ptdl.id=unit.propertydetail "
			+ INNER_JOIN_STRING
			+" eg_pt_address_v2 address on address.property=pt.id"
			+ " WHERE ";
	
	public String getPropertySearchQuery(PropertyCriteria criteria, List<Object> preparedStmtList) {
		
		StringBuilder builder = new StringBuilder(QUERY);
		builder.append("tenantid=? ");
		preparedStmtList.add(criteria.getTenantId());
		
		Set<String> ids = criteria.getIds();
		if(!CollectionUtils.isEmpty(ids)) {
			
			builder.append("and pt.id IN ("+convertSetToString(ids)+")");

		}

		Set<String> propertyDetailids = criteria.getPropertyDetailids();
		if(!CollectionUtils.isEmpty(propertyDetailids)) {

			builder.append("and ptdl.id IN ("+convertSetToString(propertyDetailids)+")");

		}

		Set<String> addressids = criteria.getAddressids();
		if(!CollectionUtils.isEmpty(addressids)) {

			builder.append("and address.id IN (").append(convertSetToString(addressids)).append(")");

		}

		Set<String> ownerids = criteria.getOwnerids();
		if(!CollectionUtils.isEmpty(ownerids)) {

			builder.append("and owner.userid IN (").append(convertSetToString(ownerids)).append(")");

		}

		Set<String> unitids = criteria.getUnitids();
		if(!CollectionUtils.isEmpty(unitids)) {

			builder.append("and unit.id IN ("+convertSetToString(unitids)+")");

		}


		Set<String> documentids = criteria.getDocumentids();
		if(!CollectionUtils.isEmpty(documentids)) {

			builder.append("and doc.id IN ("+convertSetToString(documentids)+")");

		}

		return builder.toString();
	}
	
	private String convertSetToString(Set<String> ids) {
		
		final String quotes = "'";
		final String comma = ",";
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = ids.iterator();
		while(iterator.hasNext()) {
			builder.append(quotes+iterator.next()+quotes);
			if(iterator.hasNext()) builder.append(comma);
		}
		return builder.toString();
	}
}
