package com.ikiugu.notifications.controllers;

import com.ikiugu.notifications.models.User;
import com.ikiugu.notifications.repositories.UserRepository;
import org.apache.http.util.TextUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@RestController
@RequestMapping("users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    ResponseEntity<String> testApi() {
        return new ResponseEntity<>("Working well", HttpStatus.OK);
    }

    @PostMapping("")
    ResponseEntity<User> handleUser(@RequestBody User user) {
        User newUser = new User();
        if (!TextUtils.isBlank(user.getUserName())) {
            newUser = userRepository.findByUserName(user.getUserName());
            if (newUser == null) {
                User user1 = new User();
                user1.setErrorMessage("User does not exist");
                user1.setSuccess(false);
                return new ResponseEntity<>(user1, HttpStatus.OK);
            }
        } else {
            int usersLength = userRepository.findAll().size();
            if (usersLength == 0) {
                newUser.setUserName("guest1");

            } else {
                int newId = usersLength + 1;
                newUser.setUserName("guest" + newId);
            }
        }

        newUser.setUserToken(user.getUserToken());
        newUser.setSuccess(true);
        userRepository.save(newUser);

        return new ResponseEntity<>(newUser, HttpStatus.OK);

    }
}
