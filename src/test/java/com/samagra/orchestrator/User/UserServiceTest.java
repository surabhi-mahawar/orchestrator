package com.samagra.orchestrator.User;

import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @InjectMocks
    FusionAuthClient staticClient = new FusionAuthClient("${authserver.apikey}", "${authserver.apiURL}");

    String email = "chakshu@samagragovernance.in";

    @Before
    public void setUp() {
        System.out.println("Testing");
    }

    @Test
    public void testFindByEmail(){
        User user = UserService.findByEmail(email);
        assertEquals(user.id, "4ee2ab81-6c3f-460b-94b2-de49cf55f27f");
    }

}