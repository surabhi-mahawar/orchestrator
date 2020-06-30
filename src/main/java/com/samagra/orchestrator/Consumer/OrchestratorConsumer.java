package com.samagra.orchestrator.Consumer;

import lombok.extern.slf4j.Slf4j;
import messagerosa.core.model.Transformer;
import messagerosa.core.model.TransformerRegistry;
import messagerosa.core.model.XMessage;
import messagerosa.xml.XMessageParser;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

@Slf4j
@Component
public class OrchestratorConsumer {

    @KafkaListener(id = "orchestrator", topics = "inbound-processed")
    public void consumeMessage(String message) throws Exception {
        System.out.println(message);

        XMessage msg = XMessageParser.parse(new ByteArrayInputStream(message.getBytes()));

        // Adding additional context data to the system.

        // Add user

        // Add previous messageID


        // Assign Transformer
        ArrayList<Transformer> transformers = new ArrayList<>();
        transformers.add(new Transformer("3")); //Forms
        transformers.add(new Transformer("4")); //Outbound
        msg.setTransformers(transformers); //TODO change this to Drools based
        String transformer = TransformerRegistry.getName(msg.getTransformers().get(0).getId());

        // Send message to "transformer" topic => "Forms"

    }
}
