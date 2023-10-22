package me.karanthaker.scanner;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Log4j2
@Component
public class JmsConsumer {

    @Autowired
    private ScannerService service;

    @Value("${spring.activemq.queue}")
    private String queue;

    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(10);
    }

    @JmsListener(destination = "jobs", concurrency = "10")
    public void receiveMessage(int jobId) {
        log.info("Received {} on {}", jobId, Thread.currentThread().getName());
        service.scan(jobId);
        latch().countDown();
    }
}
