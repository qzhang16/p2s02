package com.asg.p2s02;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class SecurityApp implements MessageListener {
    private static final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void onMessage(Message message) {
        try {
            Transaction trans = message.getBody(Transaction.class);
            System.out.println("monitored transaction id: " + trans.getId());
            if (trans.getId() > 7)
                latch.countDown();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            InitialContext initContext = new InitialContext();
            Topic cardTopic = (Topic) initContext.lookup("topic/cardTopic");

            try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin",
                    "admin");
                    JMSContext jmsContext = cf.createContext()) {

                JMSConsumer consumer = jmsContext.createSharedConsumer(cardTopic, "securityApp");
                consumer.setMessageListener(new SecurityApp());

                latch.await();

            }

        } catch (NamingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
