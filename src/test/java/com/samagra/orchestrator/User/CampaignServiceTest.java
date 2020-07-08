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

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CampaignServiceTest.class)
@EnableConfigurationProperties
@Import(TestConfig.class)
public class CampaignServiceTest {


    @Autowired
    public FusionAuthClient client1;

    @Mock
    private CampaignService service;

    @Before
    public void setUp() {
        System.out.println("Testing");
    }

    @Test
    public void testRetrieveApplicationData() throws Exception {
        service.staticClient = client1;
        Application application = new CampaignService().getCampaignFromID("897fb6ea-cb07-4891-b714-1e86784ef610");
        ArrayList<String> transformers = (ArrayList)application.data.get("transformers");
        Assert.assertEquals("The strings didn't match for the expected and actual transformers", "Broadcast::SMS_1", transformers.get(0));
    }
}
