package com.ikiugu.notifications.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ikiugu.notifications.models.MessageDto;
import com.ikiugu.notifications.models.NotificationDto;
import com.ikiugu.notifications.models.User;
import com.ikiugu.notifications.repositories.UserRepository;
import com.ikiugu.notifications.repositories.WeatherRepository;
import com.ikiugu.notifications.services.FirebaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@RestController
public class NotificationController {
    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final FirebaseService firebaseService;

    public NotificationController(UserRepository userRepository, WeatherRepository weatherRepository, FirebaseService firebaseService) {
        this.userRepository = userRepository;
        this.weatherRepository = weatherRepository;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<MessageDto> sendTargetedMessage(@RequestBody MessageDto request) {
        User sender = userRepository.findByUserToken(request.getSenderToken());
        User recipient = userRepository.findByUserName(request.getRecipientUserName());
        String response;
        MessageDto message = new MessageDto();

        if (sender == null) {
            message.setMessage("Sender does not exist");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        if (recipient == null) {
            message.setMessage("Recipient does not exist");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        NotificationDto notification = new NotificationDto();
        notification.setToken(recipient.getUserToken());
        notification.setSubject("New message from " + sender.getUserName());
        notification.setContent(request.getMessage());

        Map<String, String> data = new HashMap<>();
        data.put("screen", "messages");
        data.put("load_data", "true");
        notification.setData(data);

        try {
            response = firebaseService.sendNotificationWithData(notification);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            message.setMessage("Recipient does not exist");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        message.setMessage(response);
        message.setSuccess(true);
        return new ResponseEntity<>(message, HttpStatus.OK);

    }
}
