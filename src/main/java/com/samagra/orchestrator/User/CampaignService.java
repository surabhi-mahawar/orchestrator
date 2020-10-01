package com.samagra.orchestrator.User;

import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.Application;
import io.fusionauth.domain.api.ApplicationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CampaignService {

    /**
     * Retrieve Campaign Params From its Identifier
     * @param campaignID - Campaign Identifier
     * @return Application
     * @throws Exception Error Exception, in failure in Network request.
     */
    public static Application getCampaignFromID(String campaignID) throws Exception {
        FusionAuthClient staticClient = new FusionAuthClient("c0VY85LRCYnsk64xrjdXNVFFJ3ziTJ91r08Cm0Pcjbc", "http://134.209.150.161:9011");
        ClientResponse<ApplicationResponse, Void> applicationResponse = staticClient.retrieveApplication(UUID.fromString(campaignID));
        Application application = null;
        if(applicationResponse.wasSuccessful() ) {
             application = applicationResponse.successResponse.application;
        }else if (applicationResponse.exception != null) {
            Exception exception = applicationResponse.exception;
            log.error("Campaign not found with this ID ::" + exception.getMessage());
        }
        return application;
    }

    /**
     * Retrieve Campaign Params From its Name
     * @param campaignName - Campaign Name
     * @return Application
     * @throws Exception Error Exception, in failure in Network request.
     */
    public static Application getCampaignFromName(String campaignName) throws Exception {
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
