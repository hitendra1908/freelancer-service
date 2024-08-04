package com.api.freelancer.controller;

import com.api.freelancer.service.UserService;
import com.api.freelancer.user.UserApi;
import com.api.freelancer.user.UserRequestDto;
import com.api.freelancer.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/freelancer")
public class UserController implements UserApi {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());

    }

    @PostMapping("/users")
    @Override
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.save(userRequestDto));
    }

}
