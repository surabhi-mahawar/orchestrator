package com.samagra.orchestrator.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.uci.utils.CampaignService;
import io.fusionauth.domain.Application;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.Function;

public class CampaignServiceTest {

    @Test
    public void testRetrieveApplicationData() throws Exception {
//        JsonNode application = new CampaignService().getCampaignFromID("897fb6ea-cb07-4891-b714-1e86784ef610").map(new Function<JsonNode, Object>() {
//            @Override
//            public Object apply(JsonNode jsonNode) {
//                return null;
//            }
//        });
//        ArrayList<String> transformers = (ArrayList)application.data.get("transformers");
//        Assert.assertNotEquals("The strings didn't match for the expected and actual transformers", "Broadcast::SMS_1", transformers.get(0));
    }
}
