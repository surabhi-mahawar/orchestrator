package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Drools.DroolsBeanFactory;
import com.samagra.orchestrator.Publisher.CommonProducer;
import com.samagra.orchestrator.User.UserService;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.SenderReceiverInfo;
import messagerosa.core.model.TransformerRegistry;
import messagerosa.core.model.XMessage;
import messagerosa.dao.XMessageDAO;
import messagerosa.dao.XMessageRepo;
import messagerosa.xml.XMessageParser;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
public class CampaignConsumer {

    @Autowired
    public XMessageRepo xmsgRepo;

    @Autowired
    public CommonProducer kafkaProducer;

    @KafkaListener(id = "orchestrator", topics = "${campaign}")
    public void consumeMessage(String campaignID) throws Exception {
        XMessage xMessage = processMessage(campaignID);
        if(xMessage.getTransformers())
        kafkaProducer.send("broadcast", xMessage.toXML());
        else kafkaProducer.send("odk", xMessage.toXML());
        kafkaProducer.send();
    }

    public static XMessage processMessage(String campaignID) {
        // Get campaign ID and get campaign details {data: transformers [broadcast(SMS), <formID>(Whatsapp)]}

        // Create a new campaign xMessage
        // app = campaignID
        // channelURI = SMS/WhatsApp
        // Provider = Gupshup
        XMessage x = XMessage.builder().app().build();

        // If broadcast send it "broadcast" topic else send it to "odk" topic

    }
}