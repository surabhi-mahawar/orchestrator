package com.samagra.orchestrator.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
        kafkaProducer.send("com.odk.broadcast", xMessage.toXML());
    }

    /**
     * Retrieve a campaign's info from its identifier (Campaign ID)
     *
     * @param campaignID - String {Campaign Identifier}
     * @return XMessage
     */
    public static XMessage processMessage(String campaignID) throws Exception {
        // Get campaign ID and get campaign details {data: transformers [broadcast(SMS), <formID>(Whatsapp)]}
        JsonNode campaignDetails = CampaignService.getCampaignFromID(campaignID).get("data");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode adapter = campaignDetails.findValues("logic").get(0).get(0).get("adapter");

        // Create a new campaign xMessage
        XMessagePayload payload = XMessagePayload.builder().text("").build();

        String userSegmentName = ((ArrayNode) campaignDetails.get("userSegments")).get(0).get("name").asText();
        SenderReceiverInfo to = SenderReceiverInfo.builder()
                .userID(userSegmentName)
                .build();

        Transformer broadcast = Transformer.builder()
                .id("1")
                .build();
        ArrayList<Transformer> transformers = new ArrayList<>();
        transformers.add(broadcast);

        Map<String, String> metadata = new HashMap<>();
        SenderReceiverInfo from = SenderReceiverInfo.builder()
                .userID("admin")
                .meta(metadata)
                .build();

        XMessage.MessageType messageType = XMessage.MessageType.BROADCAST_TEXT;

        return XMessage.builder()
                .app(campaignDetails.get("name").asText())
                .channelURI(adapter.get("channel").asText())
                .providerURI(adapter.get("provider").asText())
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