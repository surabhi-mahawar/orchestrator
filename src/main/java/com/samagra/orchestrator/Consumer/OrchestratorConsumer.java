package com.samagra.orchestrator.Consumer;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class OrchestratorConsumer {

    @KafkaListener(id = "orchestrator", topics = "${gupshup-incoming-message}")
    public void consumeMessage(String message) throws Exception {
        System.out.println(message);
    }
}
