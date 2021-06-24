package com.samagra.orchestrator.User;

import com.fasterxml.jackson.databind.JsonNode;
import io.fusionauth.domain.Application;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CampaignServiceTest {

    @Test
    public void testRetrieveApplicationData() throws Exception {
        JsonNode application = new CampaignService().getCampaignFromID("897fb6ea-cb07-4891-b714-1e86784ef610");
//        ArrayList<String> transformers = (ArrayList)application.data.get("transformers");
//        Assert.assertNotEquals("The strings didn't match for the expected and actual transformers", "Broadcast::SMS_1", transformers.get(0));
    }
}
