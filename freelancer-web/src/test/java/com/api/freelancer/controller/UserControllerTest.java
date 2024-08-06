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
    public void shouldGetAllUsers() {
        UserResponseDto user1 = new UserResponseDto(1L,"ironMan", "Tony", "Stark", "tony.stark@example.com", null);
        UserResponseDto user2 = new UserResponseDto(2L, "batman", "Bruce", "Wayne", "bruce.wayne@example.com", null);
        List<UserResponseDto> expectedUsers = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(expectedUsers);

        List<UserResponseDto> actualUsers = userController.getAllUsers().getBody();

        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals("ironMan", actualUsers.get(0).userName());

    }

    @Test
    public void shouldSaveAndReturnUser() {
        UserRequestDto userRequestDto = new UserRequestDto("ironMan", "Tony", "Stark", "tony.stark@example.com");
        UserResponseDto expectedUserResponseDto = new UserResponseDto(1L, "ironMan", "Tony", "Stark", "tony.stark@example.com", null);

        when(userService.save(userRequestDto)).thenReturn(expectedUserResponseDto);

        UserResponseDto actualUser = userController.addUser(userRequestDto).getBody();

        assertNotNull(actualUser);
        assertEquals("ironMan", actualUser.userName());
        assertEquals("Tony", actualUser.firstName());
        assertEquals("Stark", actualUser.lastName());
        assertEquals("tony.stark@example.com", actualUser.email());
    }

    @Test
    public void shouldUpdateAndReturnUser() {
        UserRequestDto userRequestDto = new UserRequestDto("ironMan", "Tony", "Stark", "tony.stark@example.com");
        UserResponseDto expectedUserResponseDto = new UserResponseDto(1L, "ironMan", "Tony", "Stark", "tony.stark@example.com", null);

        when(userService.updateUser(1L, userRequestDto)).thenReturn(expectedUserResponseDto);

        UserResponseDto actualUser = userController.updateUser(1L, userRequestDto).getBody();

        assertNotNull(actualUser);
        assertEquals("ironMan", actualUser.userName());
        assertEquals("Tony", actualUser.firstName());
        assertEquals("Stark", actualUser.lastName());
        assertEquals("tony.stark@example.com", actualUser.email());
    }

    @Test
    public void shouldReturnUser_WhenIdIsPassed() {
        UserResponseDto user = new UserResponseDto(1L,"ironMan", "Tony", "Stark", "tony.stark@example.com", null);

        when(userService.findUserById(1L)).thenReturn(user);

        UserResponseDto actualUser = userController.getUserById(1L).getBody();

        assertNotNull(actualUser);
        assertEquals("ironMan", actualUser.userName());
        assertEquals("Tony", actualUser.firstName());
        assertEquals("Stark", actualUser.lastName());
        assertEquals("tony.stark@example.com", actualUser.email());

    }
}
