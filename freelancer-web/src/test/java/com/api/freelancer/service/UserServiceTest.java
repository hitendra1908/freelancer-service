package com.api.freelancer.service;

import com.api.freelancer.exception.user.DuplicateUserException;
import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.exception.user.UserNameException;
import com.api.freelancer.exception.user.UserNotFoundException;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void save_ShouldThrowDuplicateUserException_WhenSameUserName() {
        UserRequestDto userRequestDto = new UserRequestDto("john_doe", "John", "Doe", "john@example.com");
        when(userRepository.save(any(Users.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateUserException.class, () -> userService.save(userRequestDto));
    }

    @Test
    void update_ShouldUpdateUser() {
        UserRequestDto userRequestDto = new UserRequestDto("john_doe", "John", "Doe", "john@example.com");
        Users existingUser = Users.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(Users.class))).thenReturn(existingUser);

        UserResponseDto responseDto = userService.updateUser(1L, userRequestDto);
        assertNotNull(responseDto);
        assertEquals("john_doe", responseDto.userName());
        assertEquals("John", responseDto.firstName());
    }



    @Test
    void update_ShouldThrowUserNameException_WhenUpdatingUserName() {
        UserRequestDto userRequestDto = new UserRequestDto("jane_doe", "Jane", "Doe", "jane@example.com");
        Users existingUser = Users.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));

        assertThrows(UserNameException.class, () -> userService.updateUser(1L, userRequestDto));
    }

    @Test
    void shouldReturnUser_WhenCorrectIdIsPassed() {
        Users user = Users.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(documentsRepository.findByUserUserName(anyString())).thenReturn(List.of());

        UserResponseDto responseDto = userService.findUserById(1L);
        assertNotNull(responseDto);
        assertEquals("john_doe", responseDto.userName());
    }

    @Test
    void shouldThrowUserNotFoundException_WhenWrongIdIsPassed() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void update_ShouldThrowUserNotFoundException_WhenIdDoesNotExist() {
        UserRequestDto userRequestDto = new UserRequestDto("john_doe", "John", "Doe", "john@example.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userRequestDto));
    }

    @Test
    void save_ShouldThrowUserException_WhenUserNameIsTooShort() {
        UserRequestDto invalidRequest = new UserRequestDto("usr", "Tony", "Stark", "tony.stark@example.com");

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("username should be at least 4 characters", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenFirstNameIsEmpty() {
        UserRequestDto invalidRequest = new UserRequestDto("ironMan", "", "Stark", "tony.stark@example.com");

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("First name cannot be empty", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenEmailIsInvalid() {
        UserRequestDto invalidRequest = new UserRequestDto("ironMan", "Tony", "Stark", "tony.stark@");

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("Invalid email address", exception.getMessage());
    }
}
