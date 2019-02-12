package com.javawxid.mqTest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class BossProducer2 {

    public static void main(String[] args) {
        ConnectionFactory connect = new ActiveMQConnectionFactory("tcp://192.168.0.100:61616");
        try {
            //创建连接对象
            Connection connection = connect.createConnection();
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            //使用topic话题（订阅）
            Topic topic = session.createTopic("boss speak");
            //创建提供者
            MessageProducer producer = session.createProducer(topic);
            TextMessage textMessage=new ActiveMQTextMessage();
            textMessage.setText("我们要为中国的伟大复兴而努力奋斗！");
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit();// 事务型消息，必须提交后才生效
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
