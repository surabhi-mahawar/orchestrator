package com.samagra.orchestrator.User;

import com.samagra.orchestrator.Consumer.CampaignConsumer;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import lombok.SneakyThrows;
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
class UserServiceTest {
    String email = "chakshu@samagragovernance.in";

    @Test
    public void testFindByEmail(){
        User user = UserService.findByEmail(email);
        assertEquals("4ee2ab81-6c3f-460b-94b2-de49cf55f27f", user.id.toString());
    }

    @Test
    public void testFindUsersForCampaign() throws Exception {
        List<User> user = UserService.findUsersForCampaign("Campaign 1");
        assertNotEquals(2, user.size());
    }

    @SneakyThrows
    @Test
    public void testCampaignAddition() throws Exception {
        CampaignConsumer.processMessage("897fb6ea-cb07-4891-b714-1e86784ef610");
    }
}