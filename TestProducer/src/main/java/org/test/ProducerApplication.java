package org.test;

import static reactor.bus.selector.Selectors.$;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

@SpringBootApplication
@EnableJms
public class ProducerApplication {
	public static final String RESPONSE_QUEUE = "response";
	private static final String REQUEST_QUEUE = "request";
	
	@Autowired
	ConfigurableApplicationContext context;
	
	@Bean
	EventBus createEventBus() {
		Environment.initialize();
		EventBus bus = EventBus.create(Environment.get());

		bus.on($("message here!"), new Consumer<Event<String>>() {
			@Override
			public void accept(Event<String> ev) {
				String s = ev.getData();
				System.out.println("[" + Thread.currentThread().getName() + "] Got Message id: " + s);
				System.out.println("Killing application");
				context.close();
			}
		});

//		bus.on($("message here!"),
//				(Event<String> ev) -> {
//					String s = ev.getData();
//					System.out.println("[" + Thread.currentThread().getName() + "] Got Message id: " + s);
//					System.out.println("Killing application");
//					context.close();
//				});

		return bus;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ProducerApplication.class, args);
		
		for (String beanName : context.getBeanDefinitionNames()) {
			System.out.println("Bean: " + beanName);
		}
		
		// Send a message
        MessageCreator messageCreator = new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage("ping!");
                Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
                message.setJMSReplyTo(responseQueue);
                
                return message;
            }
        };
        
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        
        System.out.println("[" + Thread.currentThread().getName() + "]: Sending a new message.");
        jmsTemplate.send(REQUEST_QUEUE, messageCreator);
	}
}
