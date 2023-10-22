package me.karanthaker.api.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class JmsProducer {

    private final JmsTemplate jmsTemplate;
    @Value("${spring.activemq.queue}")
    private String queue;

    @Autowired
    public JmsProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    public void sendMessage(int jobId) {
        jmsTemplate.convertAndSend(queue, Integer.toString(jobId));
    }
}
