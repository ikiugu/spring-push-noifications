package com.ikiugu.notifications.services;


import com.google.firebase.messaging.*;
import com.ikiugu.notifications.models.NotificationDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@Service
public class FirebaseService {
    private final FirebaseMessaging firebaseMessaging;

    public FirebaseService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public String sendNotification(NotificationDto notification) throws FirebaseMessagingException {
        AndroidNotification androidNotification = AndroidNotification.builder()
                .setClickAction(".Notification")
                .setTitle(notification.getSubject())
                .setBody(notification.getContent())
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(androidNotification)
                .build();

        Message message = Message
                .builder()
                .setToken(notification.getToken())
                .setAndroidConfig(androidConfig)
                .build();

        return firebaseMessaging.send(message);
    }

    public String sendNotificationWithData(NotificationDto notification) throws FirebaseMessagingException {
        AndroidNotification androidNotification = AndroidNotification.builder()
                .setClickAction(".Messaging")
                .setTitle(notification.getSubject())
                .setBody(notification.getContent())
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(androidNotification)
                .build();

        Message message = Message
                .builder()
                .setToken(notification.getToken())
                .putAllData(notification.getData())
                .setAndroidConfig(androidConfig)
                .build();

        return firebaseMessaging.send(message);
    }

    public int subscribeToTopic(String topic, List<String> tokens) throws FirebaseMessagingException {
        TopicManagementResponse topicManagementResponse = firebaseMessaging.subscribeToTopic(tokens, topic);
        return topicManagementResponse.getSuccessCount();
    }

    public int unSubscribeToTopic(String topic, List<String> tokens) throws FirebaseMessagingException {
        TopicManagementResponse topicManagementResponse = firebaseMessaging.unsubscribeFromTopic(tokens, topic);
        return topicManagementResponse.getSuccessCount();
    }
}
