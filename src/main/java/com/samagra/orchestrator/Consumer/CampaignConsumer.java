package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Publisher.CommonProducer;
import com.samagra.orchestrator.User.CampaignService;
import io.fusionauth.domain.Application;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CampaignConsumer {

    private static final String SMS_BROADCAST_IDENTIFIER = "Broadcast";

    @Autowired
    public CommonProducer kafkaProducer;

    @KafkaListener(id = "campaign", topics = "campaign")
    public void consumeMessage(String campaignID) throws Exception {
        XMessage xMessage = processMessage(campaignID);
        log.info("Pushing to : "+ TransformerRegistry.getName(xMessage.getTransformers().get(0).getId()));
        kafkaProducer.send("BroadcastTransformer", xMessage.toXML());
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
        ArrayList<HashMap<String, Object>> transformerDetails = (ArrayList) campaignDetails.data.get("parts");

        // Create a new campaign xMessage
        XMessagePayload payload = XMessagePayload.builder()
                .text((String) transformerDetails.get(0).get("msg"))
                .build();

        SenderReceiverInfo to;

        try{
             to = SenderReceiverInfo.builder()
                    .userID((String) campaignDetails.data.get("group"))
                    .build();
        }catch (Exception e){
            log.info("Multiple groups found => Adding those to groups field rather than userIDs");
            to = SenderReceiverInfo.builder()
                    .groups((ArrayList<String>) campaignDetails.data.get("group"))
                    .build();
        }

        Transformer broadcast = Transformer.builder()
                .id("1")
                .build();
        ArrayList<Transformer> transformers = new ArrayList<>();
        transformers.add(broadcast);



        String userServer = "";
        try{
            userServer = (String) campaignDetails.data.get("users");
        }catch (Exception e){

        }
        Map<String, String> metadata = new HashMap<>();
        metadata.put("senderID", "HPGOVT");
        SenderReceiverInfo from = SenderReceiverInfo.builder()
                .userID("hpgovt-hpssa")
                .meta(metadata)
                .build();

        Map<String, String> metadata2 = new HashMap<>();
        metadata2.put("userServer", userServer);
        to.setMeta(metadata2);

        XMessage.MessageType messageType;
        try{
            if((Boolean) transformerDetails.get(0).get("isButtonType")){
                messageType = XMessage.MessageType.HSM_WITH_BUTTON;
            }else messageType = XMessage.MessageType.HSM;
        }catch (Exception e){
            messageType = XMessage.MessageType.HSM;
        }

        return XMessage.builder()
                .app(campaignDetails.name)
                .channelURI((String) transformerDetails.get(0).get("channel"))
                .providerURI((String) transformerDetails.get(0).get("provider"))
                .payload(payload)
                .conversationStage(new ConversationStage(0, ConversationStage.State.STARTING))
                .timestamp(System.currentTimeMillis())
                .transformers(transformers)
                .to(to)
                .messageType(messageType)
                .from(from)
                .build();
    }
}