package com.api.freelancer.controller;

import com.api.freelancer.model.Users;
import com.api.freelancer.service.UserService;
import com.api.freelancer.user.UserApi;
import com.api.freelancer.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<UserDto> getAllUsers() {
        List<Users> users =  userService.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .toList();
    }

    @PostMapping("/users")
    @Override
    public UserDto addUser(@RequestBody UserDto userDto) {

        final Users incomingUser = mapToUsers(userDto); //TODO do validation
        final Users user = userService.save(incomingUser);
        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(Users users) {
        return new UserDto( users.getUserName(), users.getFirstName(), users.getLastName(), users.getEmail());
    }

    private Users mapToUsers(UserDto userDto) {
        return Users.builder()
                .userName(userDto.userName())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .build();
    }
}
