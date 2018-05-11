package org.egov.wcms.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Import({TracerConfiguration.class})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class WaterConnectionConfig {

    @Value("${app.timezone}")
    private String timeZone;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @Bean
    public ObjectMapper objectMapper(){
    return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setTimeZone(TimeZone.getTimeZone(timeZone));
    }

    @Bean
    @Autowired
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);
    return converter;
    }
    
    //SEARCHER
    @Value("${egov.infra.searcher.host}")
    private String egovSeacherHost;
     
    @Value("${egov.infra.searcher.endpoint}")
    private String egovSearcherEndpoint;
    
    
    //USER
    @Value("${egov.user.host}")
    private String userSvcHost;
     
    @Value("${egov.user.search.endpoint}")
    private String userSearchEndpoint;
    
    
    //PERSISTER
    @Value("${persister.save.waterconnection.topic}")
    private String saveWaterConnectionTopic;
     
    @Value("${persister.update.waterconnection.topic}")
    private String updateWaterConnectionTopic;
    
    
    
    
    //EXTRA-PARAMS
}