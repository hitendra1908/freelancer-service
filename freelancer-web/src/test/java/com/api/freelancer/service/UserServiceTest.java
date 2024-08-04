package com.api.freelancer.service;

import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import com.api.freelancer.user.UserRequestDto;
import com.api.freelancer.user.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentsRepository documentsRepository;

    @InjectMocks
    private UserService userService;

    private UserRequestDto validUserRequestDto;
    private Users validUser;

    @BeforeEach
    void setUp() {
        validUserRequestDto = new UserRequestDto("ironMan", "Tony", "Stark", "tony.stark@example.com");
        validUser = Users.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
    }

    @Test
    void findAll_ShouldReturnUserResponseDtoList() {
        List<Users> users = List.of(validUser);
        when(userRepository.findAll()).thenReturn(users);
        when(documentsRepository.findByUserUserName(anyString())).thenReturn(List.of());

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ironMan", result.get(0).userName());
    }

    @Test
    void save_ShouldReturnSavedUserResponseDto() {
        when(userRepository.save(any(Users.class))).thenReturn(validUser);
        when(documentsRepository.findByUserUserName(anyString())).thenReturn(List.of());

        UserResponseDto result = userService.save(validUserRequestDto);

        assertNotNull(result);
        assertEquals("ironMan", result.userName());
    }

    @Test
    void save_ShouldThrowUserException_WhenUserNameIsTooShort() {
        UserRequestDto invalidRequest = new UserRequestDto("usr", "Tony", "Stark", "tony.stark@example.com");

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("user name should be at least 4 chars", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenFirstNameIsEmpty() {
        UserRequestDto invalidRequest = new UserRequestDto("ironMan", "", "Stark", "tony.stark@example.com");

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("first name can not be empty", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenEmailIsInvalid() {
        UserRequestDto invalidRequest = new UserRequestDto("ironMan", "Tony", "Stark", "tony.stark@");

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("invalid email address", exception.getMessage());
    }
}
