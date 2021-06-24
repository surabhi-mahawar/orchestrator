package com.samagra.orchestrator.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.Application;
import io.fusionauth.domain.api.ApplicationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class CampaignService {

    @Value("${campaign.url}")
    public String CAMPAIGN_URL;

    /**
     * Retrieve Campaign Params From its Identifier
     *
     * @param campaignID - Campaign Identifier
     * @return Application
     * @throws Exception Error Exception, in failure in Network request.
     */
    public JsonNode getCampaignFromID(String campaignID) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String baseURL = CAMPAIGN_URL + "admin/v1/bot/get/" + campaignID;
            ResponseEntity<String> response = restTemplate.getForEntity(baseURL, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response.getBody());
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Retrieve Campaign Params From its Name
     * @param campaignName - Campaign Name
     * @return Application
     * @throws Exception Error Exception, in failure in Network request.
     */
    public Application getCampaignFromName(String campaignName) throws Exception {
        List<Application> applications = new ArrayList<>();
        FusionAuthClient staticClient = new FusionAuthClient("c0VY85LRCYnsk64xrjdXNVFFJ3ziTJ91r08Cm0Pcjbc", "http://134.209.150.161:9011");
        ClientResponse<ApplicationResponse, Void> response = staticClient.retrieveApplications();
        if (response.wasSuccessful()) {
            applications = response.successResponse.applications;
        } else if (response.exception != null) {
            Exception exception = response.exception;
        }


        Application currentApplication = null;
        if(applications.size() > 0){
            for(Application application: applications){
                if(application.name.equals(campaignName)){
                    currentApplication = application;
                }
            }
        }
        return currentApplication;
    }
}
