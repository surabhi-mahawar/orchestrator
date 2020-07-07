package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Drools.DroolsBeanFactory;
import com.samagra.orchestrator.Publisher.CommonProducer;
import com.samagra.orchestrator.User.CampaignService;
import com.samagra.orchestrator.User.UserService;
import io.fusionauth.domain.Application;
import liquibase.pro.packaged.S;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.*;
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
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Component
public class CampaignConsumer {

    private static final String SMS_BROADCAST_IDENTIFIER = "Broadcast";
    @Autowired
    public XMessageRepo xmsgRepo;

    @Autowired
    public CommonProducer kafkaProducer;

    @KafkaListener(id = "orchestrator", topics = "${campaign}")
    public void consumeMessage(String campaignID) throws Exception {
        XMessage xMessage = processMessage(campaignID);
        kafkaProducer.send(TransformerRegistry.getName(xMessage.getTransformers().get(0).getId()), xMessage.toXML());
    }

    /**
     * Retrieve a campaign's info from its identifier (Campaign ID)
     *
     * @param campaignID - String {Campaign Identifier}
     * @return XMessage
     */
    public static XMessage processMessage(String campaignID) throws Exception {
        // Get campaign ID and get campaign details {data: transformers [broadcast(SMS), <formID>(Whatsapp)]}
        Application campaignDetails = CampaignService.getCampaignFromID(campaignID);
        ArrayList<String> transformerDetails = (ArrayList) campaignDetails.data.get("transformers");
        // Create a new campaign xMessage
        String channelURI = "";
        String transformerID = "";
        String key = "";
        String value = "";
        if (transformerDetails.get(0).contains(SMS_BROADCAST_IDENTIFIER)) {
            channelURI = "SMS";
            key = "Template";
            value = transformerDetails.get(0).split("::")[1];
            transformerID = "1";
        } else {
            channelURI = "WhatsApp";
            transformerID = "2";
            key = "Form";
            value = transformerDetails.get(0).split("::")[1];
        }
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put(key, value);
        Transformer transformer = new Transformer(transformerID, hashMap);
        ArrayList<Transformer> transformers = new ArrayList<>();
        transformers.add(0, transformer);
        return XMessage.builder().app(campaignID).channelURI(channelURI).
                campaignStage(new CampaignStage(0, CampaignStage.State.STARTING)).providerURI("Gupshup").
                timestamp(System.currentTimeMillis()).transformers(transformers).build();
    }
}