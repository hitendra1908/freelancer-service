package com.api.freelancer.service;

import com.api.freelancer.exception.user.DuplicateUserException;
import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.exception.user.UserNameException;
import com.api.freelancer.exception.user.UserNotFoundException;
import com.api.freelancer.model.Documents;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private Documents document;

    @BeforeEach
    void setUp() {
        validUserRequestDto = UserRequestDto.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        validUser = Users.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        document = Documents.builder()
                .id(1L)
                .name("ironMan_document")
                .documentType("testDocType")
                .user(validUser)
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
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
        assertEquals("ironMan", result.get(0).getUserName());
    }

    @Test
    void save_ShouldReturnSavedUserResponseDto() {
        when(userRepository.save(any(Users.class))).thenReturn(validUser);
        when(documentsRepository.findByUserUserName(anyString())).thenReturn(List.of());

        UserResponseDto result = userService.save(validUserRequestDto);

        assertNotNull(result);
        assertEquals("ironMan", result.getUserName());
    }

    @Test
    void save_ShouldThrowDuplicateUserException_WhenSameUserName() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(userRepository.save(any(Users.class))).thenThrow(DataIntegrityViolationException.class);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> userService.save(userRequestDto));

        assertEquals("User already exists with username: john_doe", exception.getMessage());
    }

    @Test
    void update_ShouldUpdateUser() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
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
        assertEquals("john_doe", responseDto.getUserName());
        assertEquals("John", responseDto.getFirstName());
    }

    @Test
    void update_ShouldThrowUserNameException_WhenUpdatingUserName() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .userName("jane_doe")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .build();
        Users existingUser = Users.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));

        UserException exception = assertThrows(UserNameException.class, () -> userService.updateUser(1L, userRequestDto));

        assertEquals("Changing username is not allowed", exception.getMessage());
    }

    @Test
    void shouldReturnUser_WhenUserHasNoDocument() {
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
        assertEquals("john_doe", responseDto.getUserName());
        assertTrue(responseDto.getDocuments().isEmpty());
    }

    @Test
    void shouldReturnUser_WhenUserHasDocument() {
        Users user = Users.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(documentsRepository.findByUserUserName(anyString())).thenReturn(List.of(document));

        UserResponseDto responseDto = userService.findUserById(1L);
        assertNotNull(responseDto);
        assertEquals("john_doe", responseDto.getUserName());
        assertEquals(1, responseDto.getDocuments().size());
    }

    @Test
    void shouldThrowUserNotFoundException_WhenWrongIdIsPassed() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));

        assertEquals("User you are trying to retrieve is not found", exception.getMessage());
    }

    @Test
    void update_ShouldThrowUserNotFoundException_WhenIdDoesNotExist() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userRequestDto));

        assertEquals("User you are trying to update is not found", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenUserNameIsTooShort() {
        UserRequestDto invalidRequest = UserRequestDto.builder()
                .userName("usr")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("username should be at least 4 characters", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenFirstNameIsEmpty() {
        UserRequestDto invalidRequest = UserRequestDto.builder()
                .userName("ironMan")
                .firstName("")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("First name cannot be empty", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenEmailIsInvalid() {
        UserRequestDto invalidRequest = UserRequestDto.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@")
                .build();

        UserException exception = assertThrows(UserException.class, () -> userService.save(invalidRequest));

        assertEquals("Invalid email address", exception.getMessage());
    }
}
