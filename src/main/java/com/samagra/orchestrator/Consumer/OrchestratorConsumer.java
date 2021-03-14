package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Publisher.CommonProducer;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.SenderReceiverInfo;
import messagerosa.dao.XMessageDAO;
import messagerosa.dao.XMessageRepo;
import messagerosa.xml.XMessageParser;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import messagerosa.core.model.XMessage;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

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
            kafkaProducer.send("com.odk.SamagraODKAgg", msg.toXML());
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            log.info("Total time spent in processing message CP-2: " + duration / 1000000);
        }
    }
}
