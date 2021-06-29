package com.samagra.orchestrator.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.samagra.orchestrator.Publisher.CommonProducer;
import com.uci.utils.BotService;
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
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class OrchestratorConsumer {

    @Autowired
    public KieSession kSession;

    @Autowired
    public XMessageRepo xmsgRepo;

    @Autowired
    public CommonProducer kafkaProducer;

    @Autowired
    public BotService botService;

    private final String DEFAULT_APP_NAME = "Global Bot";

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
        long finalStartTime = startTime;
        getAppName(msg.getPayload().getText(),msg.getFrom()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String appName) {
                fetchAdapterID(appName).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String adapterID) {
                        from.setCampaignID(appName);
                        from.setUserID(msg.getFrom().getUserID());
                        msg.setFrom(from);
                        msg.setApp(appName);
                        msg.setLastMessageID(getLastMessageID(msg));
                        msg.setAdapterId(adapterID);
                        // Assign Transformer
                        kSession.insert(msg);
                        kSession.fireAllRules();
                        // Send message to "transformer"
                        //TODO Do this through orchestrator
                        if(msg.getMessageState().equals(XMessage.MessageState.REPLIED) || msg.getMessageState().equals(XMessage.MessageState.OPTED_IN)){
                            try {
                                kafkaProducer.send("com.odk.SamagraODKAgg", msg.toXML());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            } catch (JAXBException e) {
                                e.printStackTrace();
                            }
                            long endTime = System.nanoTime();
                            long duration = (endTime - finalStartTime);
                            log.info("Total time spent in processing message CP-2: " + duration / 1000000);
                        }
                    }
                });
            }
        });

    }

    private String getLastMessageID(XMessage msg) {
        if(msg.getMessageType().toString().equalsIgnoreCase("text")) {
            List<XMessageDAO> msg1 =  xmsgRepo.findAllByUserIdOrderByTimestamp(msg.getFrom().getUserID());
            if (msg1.size() > 0) {
                return String.valueOf(msg1.get(0).getId());
            }
        }else if(msg.getMessageType().toString().equalsIgnoreCase("button")){
            XMessageDAO lastMessage =
                    xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(msg.getFrom().getUserID(), "SENT");
            return String.valueOf(lastMessage.getId());
        }
        return "0";
    }


    private Mono<String> fetchAdapterID(String appName) {
        return botService.getCurrentAdapter(appName);
    }

    private Mono<String> getAppName(String text,SenderReceiverInfo from) {
        String appName = DEFAULT_APP_NAME;
        try {
            String finalAppName = appName;
            return botService.getCampaignFromStartingMessage(text).map(new Function<String, String>() {
                @Override
                public String apply(String appName1) {
                    if (appName1 == null || appName1.equals("")) {
                        try {
                            XMessageDAO xMessageLast = xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT");
                            appName1 = xMessageLast.getApp();
                        } catch (Exception e2) {
                            XMessageDAO xMessageLast = xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT");
                            appName1 = xMessageLast.getApp();
                        }
                    }
                    return (appName1 == null || appName1.isEmpty()) ? finalAppName : appName1;
                }
            });
        } catch (Exception e) {
            try {
                XMessageDAO xMessageLast = xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT");
                appName = xMessageLast.getApp();
            } catch (Exception e2) {
                XMessageDAO xMessageLast = xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT");
                appName = xMessageLast.getApp();
            }
            return Mono.just(appName);
        }
    }
}
