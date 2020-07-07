package com.samagra.orchestrator.User;

import com.samagra.orchestrator.Consumer.CampaignConsumer;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes=UserServiceTest.class)
@EnableConfigurationProperties
@Import(TestConfig.class)
class UserServiceTest {
    String email = "chakshu@samagragovernance.in";

    @Autowired
    public FusionAuthClient client1;

    @Mock
    private UserService service;

    @Before
    public void setUp() {
        System.out.println("Testing");
    }

    @Test
    public void testFindByEmail(){
        service.staticClient = client1;
        User user = UserService.findByEmail(email);
        assertEquals("4ee2ab81-6c3f-460b-94b2-de49cf55f27f", user.id.toString());
    }

    @Test
    public void testFindUsersForCampaign(){
        service.staticClient = client1;
        List<User> user = UserService.findUsersForCampaign("Campaign 1");
        assertEquals(2, user.size());
    }

    @Test
    public void testCampaignAddition() throws Exception {
        // [data => {transfomers: [broadcast, formID]}]
        service.staticClient = client1;
        CampaignConsumer.processMessage("897fb6ea-cb07-4891-b714-1e86784ef610");
    }


}