package com.javawxid.mqTest;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class User1Consumer2 {

    public static void main(String[] args) {
        // 连接
        ConnectionFactory connect = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD,"tcp://192.168.0.100:61616");
        try {
            Connection connection = connect.createConnection();
            //设置客户端id
            connection.setClientID("userOne");

            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("boss speak");
            //在消费端标记，Session.createDurableSubscriber(Topic topic, String name)
            //是发布-订阅持久化的接收端的设置。
            //参数  topic -> 与发送端的topic 对应，建立连接
            //参数 name -> 为订阅者的标识(相当于id)
            MessageConsumer consumer =session.createDurableSubscriber(topic,"userOne");
            //MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if(message instanceof TextMessage){
                        try {
                            String text = ((TextMessage) message).getText();
                            System.out.println(text+",员工1这个月工资不要了。。。");

                            //session.rollback();
                            session.commit();
                        } catch (JMSException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();;
        }

    }
}
