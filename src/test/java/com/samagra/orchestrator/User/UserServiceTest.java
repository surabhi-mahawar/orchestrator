package com.samagra.orchestrator.User;

import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    String email = "chakshu@samagragovernance.in";

    @Before
    public void setUp() {
        System.out.println("Testing");
    }

    @Test
    public void testFindByEmail(){
        UserService.staticClient = new FusionAuthClient("c0VY85LRCYnsk64xrjdXNVFFJ3ziTJ91r08Cm0Pcjbc", "http://134.209.150.161:9011");
        User user = UserService.findByEmail(email);
        assertEquals("4ee2ab81-6c3f-460b-94b2-de49cf55f27f", user.id.toString());
    }

    @Test
    public void testFindUsersForCampaign(){
        UserService.staticClient = new FusionAuthClient("c0VY85LRCYnsk64xrjdXNVFFJ3ziTJ91r08Cm0Pcjbc", "http://134.209.150.161:9011");
        List<User> user = UserService.findUsersForCampaign("Campaign 1");
        assertEquals(3, user.size());
    }

}