package com.example.cryptoinsight.services;

import com.example.cryptoinsight.dto.UserDto;
import com.example.cryptoinsight.entity.User;
import com.example.cryptoinsight.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Data
public class UserService {

    private final UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity registerUser(UserDto userDto){

        if(userRepository.findByUsername(userDto.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if(userRepository.findByEmail(userDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setEmail(userDto.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
    public ResponseEntity logUser(UserDto userDto){
        User user = userRepository.findByUsername(userDto.getUsername());
        if(user == null) {
            System.out.println(ResponseEntity.badRequest().body("Username doesnt exists"));
        return ResponseEntity.badRequest().body("Username doesnt exists");
        }
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            System.out.println(ResponseEntity.badRequest().body("Password is incorrect"));
            return ResponseEntity.badRequest().body("Password is incorrect");
        }
        return ResponseEntity.ok("");
    }


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
