package com.asg.p2s02;

import java.util.Random;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class CardApp {
    public static void main(String[] args) {
        try {
            InitialContext initContext = new InitialContext();
            Topic cardTopic = (Topic) initContext.lookup("topic/cardTopic");

            try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin",
                    "admin");
                    JMSContext jmsContext = cf.createContext()) {
                        
                Transaction trans = null;
                Random rand01 = new Random(100L);
                JMSProducer producer = jmsContext.createProducer();

                for (int i = 0; i < 10; i++) {
                    trans = new Transaction();
                    trans.setId(i);
                    trans.setUid(i + 100);
                    trans.setAmount(rand01.nextDouble() * 100);
                    trans.setLocation("Loc0" + rand01.nextInt(3));

                    producer.send(cardTopic, trans);

                    Thread.sleep(5000);
                }

            }

        } catch (NamingException | InterruptedException e ) {
            e.printStackTrace();
        } 
    }

}
