package com.ikiugu.notifications.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ikiugu.notifications.models.MessageDto;
import com.ikiugu.notifications.models.NotificationDto;
import com.ikiugu.notifications.models.User;
import com.ikiugu.notifications.models.Weather;
import com.ikiugu.notifications.repositories.UserRepository;
import com.ikiugu.notifications.repositories.WeatherRepository;
import com.ikiugu.notifications.services.FirebaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ikiugu.notifications.controllers.WeatherController.WEATHER_TOPIC;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@RestController
public class NotificationController {
    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final FirebaseService firebaseService;

    private Logger logger = LoggerFactory.getLogger(NotificationController.class);

    public NotificationController(UserRepository userRepository, WeatherRepository weatherRepository, FirebaseService firebaseService) {
        this.userRepository = userRepository;
        this.weatherRepository = weatherRepository;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<MessageDto> sendTargetedMessage(@RequestBody MessageDto request) {
        User sender = userRepository.findByUserToken(request.getSenderToken());
        User recipient = userRepository.findByUserName(request.getRecipientUserName());
        String response = "No";
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
            if (recipient.isNotifications()) {
                response = firebaseService.sendNotificationWithData(notification);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            message.setMessage("Recipient does not exist");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        message.setMessage(response);
        message.setSuccess(true);
        return new ResponseEntity<>(message, HttpStatus.OK);

    }

    @PostMapping("/handleNotifications")
    public ResponseEntity<User> handleUserPreference(@RequestBody User userRequest) {

        User user = userRepository.findByUserName(userRequest.getUserName());
        if (user == null) {
            logger.info("user " + userRequest.getUserName() + " does not exist");
            User newUser = new User();
            newUser.setSuccess(false);
            newUser.setErrorMessage("User does not exist");
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        }

        if (userRequest.isNotifications()) {
            logger.info("notifications turned on for " + user.getUserName());
            user.setNotifications(true);
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            logger.info("notifications turned off for " + user.getUserName());
            user.setNotifications(false);
            userRepository.save(user);

            List<String> tokens = Collections.singletonList(user.getUserToken());
            try {
                int num = firebaseService.unSubscribeToTopic(WEATHER_TOPIC, tokens);
                logger.info(num + " token(s) un-subscribed");
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }

            Weather weather = weatherRepository.findByUser(user);
            if (weather != null) {
                logger.info("user " + user.getUserName() + " deleted from weather notifications");
                weatherRepository.delete(weather);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

    }
}
