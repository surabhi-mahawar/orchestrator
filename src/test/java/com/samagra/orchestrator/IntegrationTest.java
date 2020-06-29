package com.samagra.orchestrator;

import com.samagra.orchestrator.Drools.DroolsBeanFactory;
import messagerosa.core.model.XMessage;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class IntegrationTest {
    private KieSession kSession;

    @Before
    public void setup() {
        Resource resource = ResourceFactory.newClassPathResource("com/samagra/orchestrator/Drools/OrchestratorRules.xlsx", getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);
        System.out.println(new DroolsBeanFactory().getDrlFromExcel("com/samagra/orchestrator/Drools/OrchestratorRules.xlsx"));
    }

    @Test
    public void testChangeInChannel() throws Exception {
        XMessage xMessage = new XMessage();
        xMessage.setMessageState(XMessage.MessageState.DELIVERED);
        xMessage.setChannel("WhatsApp");
        xMessage.setTimestamp((long) 1592900000);
        System.out.println(xMessage.secondsSinceLastMessage());
        kSession.insert(xMessage);
        kSession.fireAllRules();
        System.out.println(xMessage.getTransformers());
        System.out.println(xMessage.toXML());
    }

}
