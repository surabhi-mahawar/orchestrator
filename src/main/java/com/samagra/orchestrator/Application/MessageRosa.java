package com.samagra.orchestrator.Application;


import lombok.SneakyThrows;
import lombok.extern.java.Log;
import messagerosa.dao.XMessageDAO;
import messagerosa.dao.XMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicLong;

@Log
@RestController
public class MessageRosa {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    public XMessageRepo xmsgRepo;

    @SneakyThrows
    @GetMapping("/getLastMessage")
    public XMessageDAO greeting(@RequestParam(value = "userID", required = false) String userID,
                                @RequestParam(value = "messageType", required = false) String messageType) {

        return xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(userID, messageType);
    }

    @SneakyThrows
    @GetMapping("/deleteLastMessage")
    public void deleteLastMessage(@RequestParam(value = "userID", required = false) String userID,
                                @RequestParam(value = "messageType", required = false) String messageType) {

        XMessageDAO d = xmsgRepo.findTopByUserIdAndMessageStateOrderByTimestampDesc(userID, messageType);
        xmsgRepo.delete(d);
    }
}
