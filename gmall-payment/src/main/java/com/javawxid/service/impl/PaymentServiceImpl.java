package com.javawxid.service.impl;

import com.javawxid.bean.PaymentInfo;
import com.javawxid.config.ActiveMQUtil;
import com.javawxid.mapper.PaymentInfoMapper;
import com.javawxid.service.PaymentService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;


    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {

        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo", paymentInfo.getOutTradeNo());
        paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
    }

    @Override
    public void sendPaymentSuccess(String outTradeNo, String paymentStatus,String trackingNo) {
        try {
            // 连接消息服务器
            ConnectionFactory connect = activeMQUtil.getConnectionFactory();
            Connection connection = connect.createConnection();
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            // 发送消息
            Queue testqueue = session.createQueue("PAYMENT_SUCCESS_QUEUE");

            MessageProducer producer = session.createProducer(testqueue);
            MapMessage mapMessage=new ActiveMQMapMessage();
            mapMessage.setString("out_trade_no",outTradeNo);
            mapMessage.setString("payment_status",paymentStatus);
            mapMessage.setString("tracking_no",trackingNo);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);
            session.commit();// 事务型消息，必须提交后才生效
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
