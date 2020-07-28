package com.samagra.orchestrator.User;

import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

public class CampaignServiceTest {

    @Test
    public void testRetrieveApplicationData() throws Exception {
        Application application = new CampaignService().getCampaignFromID("897fb6ea-cb07-4891-b714-1e86784ef610");
        ArrayList<String> transformers = (ArrayList)application.data.get("transformers");
        Assert.assertNotEquals("The strings didn't match for the expected and actual transformers", "Broadcast::SMS_1", transformers.get(0));
    }
}
