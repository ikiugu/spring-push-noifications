package com.ikiugu.notifications.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ikiugu.notifications.models.SubscriptionDto;
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
import java.util.List;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@RestController
public class WeatherController {
    private final WeatherRepository weatherRepository;
    private final UserRepository userRepository;
    private final FirebaseService firebaseService;
    public static final String WEATHER_TOPIC = "weather";

    Logger logger = LoggerFactory.getLogger(WeatherController.class);

    public WeatherController(WeatherRepository weatherRepository, UserRepository userRepository, FirebaseService firebaseService) {
        this.weatherRepository = weatherRepository;
        this.userRepository = userRepository;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/subscribe/weather")
    public ResponseEntity<Weather> updateSubscription(@RequestBody SubscriptionDto data) {
        User user = userRepository.findByUserName(data.getUserName());
        Weather weather = new Weather();

        if (user == null) {
            weather.setSuccess(false);
            weather.setErrorMessage("This user does not exist");

            logger.info("The user does not exist");
            return new ResponseEntity<>(weather, HttpStatus.OK);
        }

        if (data.isSubscribe()) {
            weather.setUser(user);
            handleSubscription(user.getUserToken(), true);
            weatherRepository.save(weather);

            logger.info(weather.getUser().getUserName() + " subscribed successfully");
            weather.setSuccess(true);
        } else {
            weather = weatherRepository.findByUser(user);
            handleSubscription(weather.getUser().getUserToken(), false);
            weatherRepository.delete(weather);

            logger.info(weather.getUser().getUserName() + " un-subscribed successfully");
            weather.setSuccess(true);
        }

        return new ResponseEntity<>(weather, HttpStatus.OK);
    }

    private void handleSubscription(String token, boolean subscribe) {

        List<String> tokens = Collections.singletonList(token);

        if (subscribe) {
            try {
                int num = firebaseService.subscribeToTopic(WEATHER_TOPIC, tokens);
                logger.info(num + " token(s) subscribed");
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                int num = firebaseService.unSubscribeToTopic(WEATHER_TOPIC, tokens);
                logger.info(num + " token(s) un-subscribed");
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
