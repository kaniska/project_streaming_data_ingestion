package com.test.ingestion.jms;

import org.springframework.jms.core.support.JmsGatewaySupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class NotifierImpl extends JmsGatewaySupport implements Notifier {

    @Transactional
    public void sendNotification(final TestNotification notification) {
        getJmsTemplate().convertAndSend(notification);
        System.out.println("Just sent the notification ...");
    }
}
