package com.api.freelancer.controller;

import com.api.freelancer.service.UserService;
import com.api.freelancer.user.UserRequestDto;
import com.api.freelancer.user.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserRequestDto userRequestDto;
    private UserResponseDto user1;
    private UserResponseDto user2;

    @BeforeEach
    void setup() {
        userRequestDto = UserRequestDto.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        user1 = UserResponseDto.builder()
                .id(1L)
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .documents(null)
                .build();
        user1 = UserResponseDto.builder()
                .id(2L)
                .userName("batman")
                .firstName("Bruce")
                .lastName("Wayne")
                .email("bruce.wayne@example.com")
                .documents(null)
                .build();
    }

    @Test
    public void shouldGetAllUsers() {
        List<UserResponseDto> expectedUsers = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(expectedUsers);

        ResponseEntity<List<UserResponseDto>> actualUsers = userController.getAllUsers();

        assertEquals(ResponseEntity.ok(expectedUsers), actualUsers);
        verify(userService, times(1)).findAll();

    }

    @Test
    public void shouldSaveAndReturnUser() {

        when(userService.save(userRequestDto)).thenReturn(user1);

        ResponseEntity<UserResponseDto> actualUser = userController.addUser(userRequestDto);

        assertEquals(ResponseEntity.ok(user1), actualUser);
        verify(userService, times(1)).save(userRequestDto);
    }

    @Test
    public void shouldUpdateAndReturnUser() {
        when(userService.updateUser(1L, userRequestDto)).thenReturn(user1);

        ResponseEntity<UserResponseDto> actualUser = userController.updateUser(1L, userRequestDto);

        assertEquals(ResponseEntity.ok(user1), actualUser);
        verify(userService, times(1)).updateUser(1L, userRequestDto);
    }

    @Test
    public void shouldReturnUser_WhenIdIsPassed() {
        when(userService.findUserById(1L)).thenReturn(user1);

        ResponseEntity<UserResponseDto> actualUser = userController.getUserById(1L);

        assertEquals(ResponseEntity.ok(user1), actualUser);
        verify(userService, times(1)).findUserById(1L);

    }
}
