package com.samagra.orchestrator.Consumer;

import com.samagra.orchestrator.Publisher.CommonProducer;
import messagerosa.core.model.XMessage;
import messagerosa.dao.XMessageDAO;
import messagerosa.dao.XMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class DataCorrectionConsumer {
    @Autowired
    public XMessageRepo xmsgRepo;

    @KafkaListener(id = "datacorrection", topics = "${inboundProcessed}")
    public void consumeMessage(XMessage message) throws Exception {
        if(message.getMessageState().equals(XMessage.MessageState.REPLIED)){
//            String gsmsid = message.getMessageId().getGupshupMessageId();
//            XMessageDAO msgDao = xmsgRepo.findByGupShupMessageId(gsmsid);
//            String xmsgText = message.getPayload().getText();
//            msgDao.setXMessage(xmsgText);
//            xmsgRepo.save(msgDao);
        }
    }
}
