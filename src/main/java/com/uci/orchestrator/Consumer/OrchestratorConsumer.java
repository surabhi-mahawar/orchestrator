package com.uci.orchestrator.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uci.dao.models.XMessageDAO;
import com.uci.dao.repository.XMessageRepository;
import com.uci.utils.BotService;
import com.uci.utils.CommonProducer;
import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.SenderReceiverInfo;
import messagerosa.xml.XMessageParser;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import messagerosa.core.model.XMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class OrchestratorConsumer {

    @Autowired
    public KieSession kSession;

    @Autowired
    public XMessageRepository xmsgRepo;

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
//        XMessageDAO lastMessage = xmsgRepo.findFirstByFromIdOrderByTimestampDesc(msg.getFrom().getUserID());
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
                        getLastMessageID(msg).subscribe(lastMessageID -> {
                            msg.setLastMessageID(lastMessageID);
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
                        });

                    }
                });
            }
        });

    }

    private Flux<String> getLastMessageID(XMessage msg) {
        if(msg.getMessageType().toString().equalsIgnoreCase("text")) {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1L);
            return Flux.empty();
//            return xmsgRepo.findAllByUserIdAndTimestampAfter(msg.getFrom().getUserID(), yesterday).map(new Function<List<XMessageDAO>, String>() {
//                @Override
//                public String apply(List<XMessageDAO> msg1) {
//                    if (msg1.size() > 0) {
//                        return String.valueOf(msg1.get(0).getId());
//                    }
//                    return "0";
//                }
//            });

        }else if(msg.getMessageType().toString().equalsIgnoreCase("button")){
            return xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(msg.getFrom().getUserID(), "SENT").map(new Function<XMessageDAO, String>() {
                @Override
                public String apply(XMessageDAO lastMessage) {
                    return String.valueOf(lastMessage.getId());
                }
            });
        }
        return Flux.empty();
    }


    private Mono<String> fetchAdapterID(String appName) {
        return botService.getCurrentAdapter(appName);
    }

    private Mono<String> getAppName(String text,SenderReceiverInfo from) {
        String default_app_name = DEFAULT_APP_NAME;
        try {
            String finalAppName = default_app_name;
            return botService.getCampaignFromStartingMessage(text).flatMap(new Function<String, Mono<? extends String>>() {
                @Override
                public Mono<String> apply(String appName1) {
                    if (appName1 == null || appName1.equals("")) {
                        try {
                            return xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT").next().map(new Function<XMessageDAO, String>() {
                                @Override
                                public String apply(XMessageDAO xMessageLast) {
                                    return (xMessageLast.getApp()== null || xMessageLast.getApp().isEmpty()) ? finalAppName : xMessageLast.getApp();
                                }
                            });
                        } catch (Exception e2) {
                            return xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT").next().map(new Function<XMessageDAO, String>() {
                                @Override
                                public String apply(XMessageDAO xMessageLast) {
                                    return (xMessageLast.getApp()== null || xMessageLast.getApp().isEmpty()) ? finalAppName : xMessageLast.getApp();
                                }
                            });
                        }
                    }
                    return (appName1== null || appName1.isEmpty()) ? Mono.just(finalAppName) : Mono.just(appName1);
                }
            });


        } catch (Exception e) {
            try {
                return xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT").next().map(new Function<XMessageDAO, String>() {
                    @Override
                    public String apply(XMessageDAO xMessageLast) {
                        return xMessageLast.getApp();
                    }
                });
            } catch (Exception e2) {
                return xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(from.getUserID(), "SENT").next().map(new Function<XMessageDAO, String>() {
                    @Override
                    public String apply(XMessageDAO xMessageLast) {
                        return xMessageLast.getApp();
                    }
                });
            }
        }
    }
}
