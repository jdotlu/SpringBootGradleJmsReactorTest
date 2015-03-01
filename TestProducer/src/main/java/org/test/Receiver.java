package org.test;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class Receiver {
	@Autowired
	EventBus eventBus;
	
	@JmsListener(destination = ProducerApplication.RESPONSE_QUEUE)
    public void receiveMessage(TextMessage message, Session session) throws JMSException {
        System.out.println("[" + Thread.currentThread().getName() + "] Received <" + message.getText() + ">");
        
        eventBus.notify("message here!", Event.wrap(message.getJMSMessageID()));
    }
}
