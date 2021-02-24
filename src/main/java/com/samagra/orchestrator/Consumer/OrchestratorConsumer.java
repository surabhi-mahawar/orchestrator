package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Drools.DroolsBeanFactory;
import com.samagra.orchestrator.Publisher.CommonProducer;
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
import messagerosa.core.model.XMessage;
import messagerosa.xml.XMessageParser;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

@Slf4j
@Component
public class OrchestratorConsumer {

    @Autowired
    public KieSession kSession;

    @Autowired
    public XMessageRepo xmsgRepo;

    @Autowired
    public CommonProducer kafkaProducer;

    @KafkaListener(id = "orchestrator1", topics = "${inboundProcessed}")
    public void consumeMessage(String message) throws Exception {
        long startTime = System.nanoTime();
        XMessage msg = XMessageParser.parse(new ByteArrayInputStream(message.getBytes()));

        // Adding additional context data to the system.
        XMessageDAO lastMessage = xmsgRepo.findFirstByFromIdOrderByTimestampDesc(msg.getFrom().getUserID());
        long endTime1 = System.nanoTime();
        long duration1 = (endTime1 - startTime);
        log.info("Total time spent in processing message CP-1: " + duration1 / 1000000);
        startTime = System.nanoTime();
        SenderReceiverInfo from = msg.getFrom();
        from.setCampaignID(msg.getApp());
        from.setUserID(msg.getFrom().getUserID());

        // Add user
        msg.setFrom(from);

        // Add previous messageID
        msg.setLastMessageID(lastMessage.getMessageId());

        // Assign Transformer
        kSession.insert(msg);
        kSession.fireAllRules();

        // Send message to "transformer"
        //TODO Do this through orchestrator
        if(msg.getMessageState().equals(XMessage.MessageState.REPLIED) || msg.getMessageState().equals(XMessage.MessageState.OPTED_IN)){
            kafkaProducer.send("Form2", msg.toXML());
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            log.info("Total time spent in processing message CP-2: " + duration / 1000000);
        }
    }
}
