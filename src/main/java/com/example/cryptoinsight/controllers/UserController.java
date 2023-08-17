package com.example.cryptoinsight.controllers;

import com.example.cryptoinsight.dto.UserDto;
import com.example.cryptoinsight.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }

    @CrossOrigin(origins = {"https://www.cryptoinsight360.com", "http://localhost:8081"})
    @PostMapping("/users/register")
    public ResponseEntity registerUser(@RequestBody UserDto userDto) {
        logger.info("Registering user with username: " + userDto.getUsername());

        return userService.registerUser(userDto);
      }

    @CrossOrigin(origins = {"https://www.cryptoinsight360.com", "http://localhost:8081"})
    @PostMapping("/users/login")
    public ResponseEntity logUser(@RequestBody UserDto userDto) {
        logger.info("Logging user with username: " + userDto.getUsername());

        return  userService.logUser(userDto);
    }



}