package com.samagra.orchestrator.User;

import com.samagra.orchestrator.Consumer.CampaignConsumer;
import io.fusionauth.domain.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

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