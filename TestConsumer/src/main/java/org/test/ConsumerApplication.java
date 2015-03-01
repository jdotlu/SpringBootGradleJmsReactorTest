package org.test;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;

@SpringBootApplication
@EnableJms
public class ConsumerApplication {
	private static final String REQUEST_QUEUE = "request";
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ConsumerApplication.class, args);
		
		for (String beanName : context.getBeanDefinitionNames()) {
			System.out.println("Bean: " + beanName);
		}
	}
	
	@JmsListener(destination = REQUEST_QUEUE)
    public TextMessage receiveMessage(TextMessage message, Session session) throws JMSException {
        System.out.println("Received <" + message.getText() + ">");
        
        System.out.println("Send <pong!>");
        TextMessage responseMessage = session.createTextMessage("pong!");
        return responseMessage;
    }
}
