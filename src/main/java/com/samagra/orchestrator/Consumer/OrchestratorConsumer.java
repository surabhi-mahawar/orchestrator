package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Drools.DroolsBeanFactory;
import com.samagra.orchestrator.User.UserService;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.SenderReceiverInfo;
import messagerosa.core.model.Transformer;
import messagerosa.core.model.TransformerRegistry;
import messagerosa.dao.XMessageDAO;
import messagerosa.dao.XMessageRepo;
import messagerosa.xml.XMessageParser;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.XMessage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

@Slf4j
@Service
public class OrchestratorConsumer {

    private KieSession kSession;

    @Autowired
    public XMessageRepo xmsgRepo;

    @KafkaListener(id = "orchestrator", topics = "${inboundProcessed}")
    public void consumeMessage(String message) throws Exception {
        System.out.println(message);
        Resource resource = ResourceFactory.newClassPathResource("com/samagra/orchestrator/Drools/OrchestratorRules.xlsx", getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);
        System.out.println(new DroolsBeanFactory().getDrlFromExcel("com/samagra/orchestrator/Drools/OrchestratorRules.xlsx"));

        XMessage msg = XMessageParser.parse(new ByteArrayInputStream(message.getBytes()));

        // Adding additional context data to the system.
        String id = UserService.findByEmail(msg.getFrom().getUserID()).id.toString();
        XMessageDAO lastMessage = xmsgRepo.findAllByUserId(id).get(0);

        SenderReceiverInfo from = new SenderReceiverInfo();
        from.setCampaignId(msg.getApp());
        from.setUserID(id);
        // Add user
        msg.setFrom(from);

        // Add previous messageID
        msg.setLastMessageID(lastMessage.getWhatsappMessageId());

        // Assign Transformer
        kSession.insert(msg);
        kSession.fireAllRules();

        // Send message to "transformer"


    }
}
