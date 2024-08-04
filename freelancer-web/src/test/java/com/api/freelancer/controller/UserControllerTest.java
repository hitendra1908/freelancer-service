package com.api.freelancer.controller;

import com.api.freelancer.service.UserService;
import com.api.freelancer.user.UserRequestDto;
import com.api.freelancer.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        UserResponseDto user1 = new UserResponseDto("ironMan", "Tony", "Stark", "tony.stark@example.com", null);
        UserResponseDto user2 = new UserResponseDto("batman", "Bruce", "Wayne", "bruce.wayne@example.com", null);
        List<UserResponseDto> expectedUsers = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(expectedUsers);

        List<UserResponseDto> actualUsers = userController.getAllUsers().getBody();

        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals("ironMan", actualUsers.get(0).userName());

    }

    @Test
    public void testAddUser() {
        UserRequestDto userRequestDto = new UserRequestDto("ironMan", "Tony", "Stark", "tony.stark@example.com");
        UserResponseDto expectedUserResponseDto = new UserResponseDto("ironMan", "Tony", "Stark", "tony.stark@example.com", null);

        when(userService.save(userRequestDto)).thenReturn(expectedUserResponseDto);

        UserResponseDto actualResponse = userController.addUser(userRequestDto).getBody();

        assertNotNull(actualResponse);
        assertEquals("ironMan", actualResponse.userName());
    }
}
