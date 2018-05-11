package org.egov.wcms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.wcms.config.WaterConnectionConfig;
import org.egov.wcms.producer.Producer;
import org.egov.wcms.repository.Repository;
import org.egov.wcms.util.ResponseInfoFactory;
import org.egov.wcms.util.WaterConnectionServiceUtils;
import org.egov.wcms.util.WaterConnectionConstants;
import org.egov.wcms.web.models.AuditDetails;
import org.egov.wcms.web.models.Connection;
import org.egov.wcms.web.models.SearcherRequest;
import org.egov.wcms.web.models.WaterConnectionReq;
import org.egov.wcms.web.models.WaterConnectionRes;
import org.egov.wcms.web.models.WaterConnectionSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class WaterConnectionService {
	
	@Autowired
	private WaterConnectionServiceUtils wCServiceUtils;
	
	@Autowired
	private Repository wcRepository;
	
	@Autowired
	private Producer waterConnectionProducer;
	
	@Autowired
	private WaterConnectionConfig mainConfiguration;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	/**
	 * Searches Water Connections based on the criteria in WaterConnectionSearchCriteria.
	 * 
	 * @param waterConnectionSearchCriteria
	 * @param requestInfo
	 * @return WaterConnectionRes
	 */
	public WaterConnectionRes getWaterConnections(WaterConnectionSearchCriteria waterConnectionSearchCriteria, RequestInfo requestInfo) {
		StringBuilder uri = new StringBuilder();
		Object response = null;
		WaterConnectionRes waterConnectionRes = null;
		ObjectMapper mapper = wCServiceUtils.getObjectMapper();
		enrichSearchRequest(waterConnectionSearchCriteria, requestInfo);
		SearcherRequest searcherRequest = wCServiceUtils.getSearcherRequest(uri, waterConnectionSearchCriteria, requestInfo, 
				(!CollectionUtils.isEmpty(waterConnectionSearchCriteria.getOwnerIds()) ? WaterConnectionConstants.SEARCHER_WC_ON_OWNER_SEARCH_DEF_NAME : WaterConnectionConstants.SEARCHER_WC_SEARCH_DEF_NAME));
		try {
			response = wcRepository.fetchResult(uri, searcherRequest);
			if(null == response) {
				return wCServiceUtils.getDefaultWaterConnectionResponse(requestInfo);
			}
			waterConnectionRes = mapper.convertValue(response, WaterConnectionRes.class);
		}catch(Exception e) {
			log.error("Exception: " + e);
			return wCServiceUtils.getDefaultWaterConnectionResponse(requestInfo);
		}
		return waterConnectionRes;
	}
	
	/**
	 * Creates water connection in the postgres db through persister and pushes the same to elasticsearch through indexer. Both consumers pick from the same topic.
	 * @param connections
	 * @return WaterConnectionRes
	 */
	public WaterConnectionRes createWaterConnections(WaterConnectionReq connections) {
		
		enrichCreateRequest(connections);
		waterConnectionProducer.push(mainConfiguration.getSaveWaterConnectionTopic(), connections);
		return WaterConnectionRes.builder().connections(connections.getConnections())
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(connections.getRequestInfo(), true))
				.build();
	}
	
	/**
	 * Request enrichment for create flow based on the following use-cases:
	 * 1. Generate ids for each request using idGen. 
	 * @param connections
	 */
	public void enrichCreateRequest(WaterConnectionReq connections) {
		// (1)
		RequestInfo requestInfo = connections.getRequestInfo();
		AuditDetails auditDetails = wCServiceUtils.getAuditDetails(requestInfo.getUserInfo().getId().toString(), true);
		
		for(Connection connection: connections.getConnections()) {
			connection.setConnectionNumber(wCServiceUtils.generateConnectonNumber());
			connection.setId(UUID.randomUUID().toString());
			connection.getMeter().setId(UUID.randomUUID().toString());
			connection.getAddress().setId(UUID.randomUUID().toString());
			connection.setAuditDetails(auditDetails);
		}
	}
	
	/**
	 * Updates the water connection in postgres db through persister and pushes the same to elasticsearch through indexer. Both consumers pick from the same topic.
	 * @param connections
	 * @return
	 */
	public WaterConnectionRes updateWaterConnections(WaterConnectionReq connections) {
		enrichUpdateRequest(connections);
		waterConnectionProducer.push(mainConfiguration.getUpdateWaterConnectionTopic(), connections);
		return WaterConnectionRes.builder().connections(connections.getConnections()).
				responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(connections.getRequestInfo(), true)).build();	
	}

	/**
	 * Request enrichment for update flow based on the following use-cases:
	 * 
	 * @param connections
	 */
	public void enrichUpdateRequest(WaterConnectionReq connections) {
		return;
	}
	
	/**
	 * Request enrichment for search flow based on the following use-cases:
	 * 1. Fetch the user id from user-svc if the search criteria contains mobile number
	 * 
	 * @param waterConnectionSearchCriteria
	 * @param requestInfo
	 */
	public void enrichSearchRequest(WaterConnectionSearchCriteria waterConnectionSearchCriteria, RequestInfo requestInfo) {
		StringBuilder uri = new StringBuilder();
		ObjectMapper mapper = wCServiceUtils.getObjectMapper();

		// (1)
		if(!StringUtils.isEmpty(waterConnectionSearchCriteria.getPhone())){
			HashMap<String, Object> userSearchRequest = wCServiceUtils.getUserSearchRequest(uri, requestInfo, 
					waterConnectionSearchCriteria.getTenantId(), waterConnectionSearchCriteria.getPhone());
			Integer userId = null;
			try {
				Object response = wcRepository.fetchResult(uri, userSearchRequest);
				if(null != response) {
					userId = JsonPath.read(mapper.writeValueAsString(response), "$.user[0].id");
				}
			}catch(Exception e) {
				log.error("Exception while fetching userId from user svc: "+e);
			}
			if(null != userId) {
				if(!CollectionUtils.isEmpty(waterConnectionSearchCriteria.getOwnerIds())) {
					waterConnectionSearchCriteria.getOwnerIds().add(userId);
				}else {
					List<Integer> ownerIds = new ArrayList<>();
					ownerIds.add(userId);
					waterConnectionSearchCriteria.setOwnerIds(ownerIds);
				}
			}
		//
			
		}
	}

}
 