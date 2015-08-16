package com.test.ingestion.jms;

public interface Notifier {

    public void sendNotification(TestNotification notification);
}
