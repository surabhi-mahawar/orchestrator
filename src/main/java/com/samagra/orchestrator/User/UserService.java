package com.samagra.orchestrator.User;

import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.Application;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.ApplicationResponse;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.user.SearchRequest;
import io.fusionauth.domain.api.user.SearchResponse;
import io.fusionauth.domain.search.UserSearchCriteria;
import messagerosa.core.model.SenderReceiverInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class UserService {

    @Autowired
    public FusionAuthClient client;

    static FusionAuthClient staticClient;

    @Autowired
    public void setStaticClient(FusionAuthClient client) {
        UserService.staticClient = client;
    }


    public static User findByEmail(String email) {
        ClientResponse<UserResponse, Errors> response = staticClient.retrieveUserByEmail(email);
        if (response.wasSuccessful()) {
            return response.successResponse.user;
        } else if (response.errorResponse != null) {
            Errors errors = response.errorResponse;
        } else if (response.exception != null) {
            // Exception Handling
            Exception exception = response.exception;
        }

        return null;
    }

    public static List<User> findUsersForCampaign(String campaignID){

        List<Application> applications = new ArrayList<>();
        ClientResponse<ApplicationResponse, Void> response = staticClient.retrieveApplications();
        if (response.wasSuccessful()) {
            applications = response.successResponse.applications;
        } else if (response.exception != null) {
            Exception exception = response.exception;
        }


        Application currentApplication = null;
        if(applications.size() > 0){
            for(Application application: applications){
                if(application.name.equals(campaignID)){
                    currentApplication = application;
                }
            }
        }

        if(currentApplication != null){
            UserSearchCriteria usc = new UserSearchCriteria();
            usc.queryString = "(registrations.applicationId: " + currentApplication.id.toString() + ")";
            SearchRequest sr = new SearchRequest(usc);
            ClientResponse<SearchResponse, Errors> cr = staticClient.searchUsersByQueryString(sr);

            if (response.wasSuccessful()) {
                return cr.successResponse.users;
            } else if (response.exception != null) {
                // Exception Handling
                Exception exception = response.exception;
            }
        }
        return new ArrayList<>();
    }

    public static SenderReceiverInfo getInfoForUser(){
        return null;
    }
}
