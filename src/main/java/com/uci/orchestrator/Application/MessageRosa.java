package com.uci.orchestrator.Application;


import com.uci.dao.models.XMessageDAO;
import com.uci.dao.repository.XMessageRepository;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Log
@RestController
public class MessageRosa {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    public XMessageRepository xmsgRepo;

    @SneakyThrows
    @GetMapping("/getLastMessage")
    public Flux<XMessageDAO> greeting(@RequestParam(value = "replyId") String replyId, @RequestParam(value = "userId") String userId) {
        return xmsgRepo.findFirstByUserIdAndCauseIdAndMessageStateOrderByTimestampDesc(userId, replyId, "SENT");
    }

    @SneakyThrows
    @GetMapping("/deleteLastMessage")
    public void deleteLastMessage(@RequestParam(value = "userID", required = false) String userID,
                                @RequestParam(value = "messageType", required = false) String messageType) {

        xmsgRepo.findAllByUserIdAndTimestampAfter(userID, LocalDateTime.now().minusDays(1)).filter(new Predicate<XMessageDAO>() {
                    @Override
                    public boolean test(XMessageDAO xMessageDAO) {
                        return xMessageDAO.getMessageState().equals(messageType);
                    }
                }).sort(new Comparator<XMessageDAO>() {
                    @Override
                    public int compare(XMessageDAO o1, XMessageDAO o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                }).next().subscribe(new Consumer<XMessageDAO>() {
            @Override
            public void accept(XMessageDAO xMessageDAO) {
                xmsgRepo.delete(xMessageDAO);
            }
        });
    }
}
