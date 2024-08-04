package com.api.freelancer.controller;

import com.api.freelancer.exception.user.UserException;
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
import java.util.regex.Pattern;

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
        validateIncomingUser(userDto);
        final Users incomingUser = mapToUsers(userDto); //TODO do validation
        final Users savedUser = userService.save(incomingUser);
        return mapToUserDto(savedUser);
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

    private void validateIncomingUser(UserDto userDto) {

        if(userDto.userName() == null || userDto.userName().length() < 4) {
            throw new UserException("user name should be at least 4 chars");
        }
        if(userDto.firstName() == null || userDto.firstName().isEmpty()) {
            throw new UserException("first name can not be empty");
        }
        if(!validateEmailAddress(userDto.email())) {
            throw new UserException("invalid email address");
        }
    }

    public boolean validateEmailAddress(String emailAddress) {
        final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
