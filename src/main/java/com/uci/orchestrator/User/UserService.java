package com.uci.orchestrator.User;

import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import com.uci.utils.CampaignService;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.Application;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.user.SearchRequest;
import io.fusionauth.domain.api.user.SearchResponse;
import io.fusionauth.domain.search.UserSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.SenderReceiverInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    public static CampaignService campaignService;

    public FusionAuthClient fusionAuthClient;

    public User findByEmail(String email) {
        ClientResponse<UserResponse, Errors> response = fusionAuthClient.retrieveUserByEmail(email);
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

    public User findByPhone(String phone) {
        UserSearchCriteria usc = new UserSearchCriteria();
        usc.queryString = "*" + phone + "*";
        SearchRequest sr = new SearchRequest(usc);
        ClientResponse<SearchResponse, Errors> cr = fusionAuthClient.searchUsersByQueryString(sr);

        if (cr.wasSuccessful() && cr.successResponse.users.size() > 0) {
            return cr.successResponse.users.get(0);
        } else if (cr.exception != null) {
            // Exception Handling
            Exception exception = cr.exception;
            log.error("Exception in getting users for campaign: " + exception.toString());
        }
        return null;
    }

    public List<User> findUsersForCampaign(String campaignName) throws Exception {

        Application currentApplication = campaignService.getCampaignFromName(campaignName);
        if(currentApplication != null){
            UserSearchCriteria usc = new UserSearchCriteria();
            usc.queryString = "(registrations.applicationId: " + currentApplication.id.toString() + ")";
            SearchRequest sr = new SearchRequest(usc);
            ClientResponse<SearchResponse, Errors> cr = fusionAuthClient.searchUsersByQueryString(sr);

            if (cr.wasSuccessful()) {
                return cr.successResponse.users;
            } else if (cr.exception != null) {
                // Exception Handling
                Exception exception = cr.exception;
                log.error("Exception in getting users for campaign: " + exception.toString());
            }
        }
        return new ArrayList<>();
    }

    public static SenderReceiverInfo getInfoForUser(){
        return null;
    }
}
