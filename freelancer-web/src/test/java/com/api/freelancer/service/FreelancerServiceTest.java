package com.api.freelancer.service;

import com.api.freelancer.entity.Documents;
import com.api.freelancer.entity.Freelancer;
import com.api.freelancer.exception.freelancer.DuplicateUserException;
import com.api.freelancer.exception.freelancer.UserException;
import com.api.freelancer.exception.freelancer.UserNameException;
import com.api.freelancer.exception.freelancer.UserNotFoundException;
import com.api.freelancer.freelancer.FreelancerRequestDto;
import com.api.freelancer.freelancer.FreelancerResponseDto;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.FreelancerRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FreelancerServiceTest {

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private DocumentsRepository documentsRepository;

    @InjectMocks
    private FreelancerService freelancerService;

    private FreelancerRequestDto validFreelancerRequestDto;
    private Freelancer validFreelancer;
    private Documents document;

    @BeforeEach
    void setUp() {
        validFreelancerRequestDto = FreelancerRequestDto.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        validFreelancer = Freelancer.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        document = Documents.builder()
                .id(1L)
                .name("ironMan_document")
                .documentType("testDocType")
                .freelancer(validFreelancer)
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
                .build();
    }

    @Test
    void findAll_ShouldReturnUserResponseDtoList() {
        List<Freelancer> freelancers = List.of(validFreelancer);
        when(freelancerRepository.findAll()).thenReturn(freelancers);
        when(documentsRepository.findByFreelancerUserName(anyString())).thenReturn(List.of());

        List<FreelancerResponseDto> result = freelancerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ironMan", result.get(0).getUserName());
    }

    @Test
    void save_ShouldReturnSavedUserResponseDto() {
        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(validFreelancer);
        when(documentsRepository.findByFreelancerUserName(anyString())).thenReturn(List.of());

        FreelancerResponseDto result = freelancerService.save(validFreelancerRequestDto);

        assertNotNull(result);
        assertEquals("ironMan", result.getUserName());
    }

    @Test
    void save_ShouldThrowDuplicateUserException_WhenSameUserName() {
        FreelancerRequestDto freelancerRequestDto = FreelancerRequestDto.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(freelancerRepository.save(any(Freelancer.class))).thenThrow(DataIntegrityViolationException.class);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> freelancerService.save(freelancerRequestDto));

        assertEquals("User already exists with username: john_doe", exception.getMessage());
    }

    @Test
    void update_ShouldUpdateUser() {
        FreelancerRequestDto freelancerRequestDto = FreelancerRequestDto.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        Freelancer existingFreelancer = Freelancer.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .build();
        when(freelancerRepository.findById(anyLong())).thenReturn(Optional.of(existingFreelancer));
        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(existingFreelancer);

        FreelancerResponseDto responseDto = freelancerService.updateUser(1L, freelancerRequestDto);
        assertNotNull(responseDto);
        assertEquals("john_doe", responseDto.getUserName());
        assertEquals("John", responseDto.getFirstName());
    }

    @Test
    void update_ShouldThrowUserNameException_WhenUpdatingUserName() {
        FreelancerRequestDto freelancerRequestDto = FreelancerRequestDto.builder()
                .userName("jane_doe")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .build();
        Freelancer existingFreelancer = Freelancer.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .build();
        when(freelancerRepository.findById(anyLong())).thenReturn(Optional.of(existingFreelancer));

        UserException exception = assertThrows(UserNameException.class, () -> freelancerService.updateUser(1L, freelancerRequestDto));

        assertEquals("Changing username is not allowed", exception.getMessage());
    }

    @Test
    void shouldReturnUser_WhenUserHasNoDocument() {
        Freelancer freelancer = Freelancer.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(freelancerRepository.findById(anyLong())).thenReturn(Optional.of(freelancer));
        when(documentsRepository.findByFreelancerUserName(anyString())).thenReturn(List.of());

        FreelancerResponseDto responseDto = freelancerService.findUserById(1L);
        assertNotNull(responseDto);
        assertEquals("john_doe", responseDto.getUserName());
        assertTrue(responseDto.getDocuments().isEmpty());
    }

    @Test
    void shouldReturnUser_WhenUserHasDocument() {
        Freelancer freelancer = Freelancer.builder()
                .id(1L)
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(freelancerRepository.findById(anyLong())).thenReturn(Optional.of(freelancer));
        when(documentsRepository.findByFreelancerUserName(anyString())).thenReturn(List.of(document));

        FreelancerResponseDto responseDto = freelancerService.findUserById(1L);
        assertNotNull(responseDto);
        assertEquals("john_doe", responseDto.getUserName());
        assertEquals(1, responseDto.getDocuments().size());
    }

    @Test
    void shouldThrowUserNotFoundException_WhenWrongIdIsPassed() {
        when(freelancerRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> freelancerService.findUserById(1L));

        assertEquals("User you are trying to retrieve is not found", exception.getMessage());
    }

    @Test
    void update_ShouldThrowUserNotFoundException_WhenIdDoesNotExist() {
        FreelancerRequestDto freelancerRequestDto = FreelancerRequestDto.builder()
                .userName("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        when(freelancerRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> freelancerService.updateUser(1L, freelancerRequestDto));

        assertEquals("User you are trying to update is not found", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenUserNameIsTooShort() {
        FreelancerRequestDto invalidRequest = FreelancerRequestDto.builder()
                .userName("usr")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        UserException exception = assertThrows(UserException.class, () -> freelancerService.save(invalidRequest));

        assertEquals("username should be at least 4 characters", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenFirstNameIsEmpty() {
        FreelancerRequestDto invalidRequest = FreelancerRequestDto.builder()
                .userName("ironMan")
                .firstName("")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        UserException exception = assertThrows(UserException.class, () -> freelancerService.save(invalidRequest));

        assertEquals("First name cannot be empty", exception.getMessage());
    }

    @Test
    void save_ShouldThrowUserException_WhenEmailIsInvalid() {
        FreelancerRequestDto invalidRequest = FreelancerRequestDto.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@")
                .build();

        UserException exception = assertThrows(UserException.class, () -> freelancerService.save(invalidRequest));

        assertEquals("Invalid email address", exception.getMessage());
    }

    @Test
    void shouldThrowUserNotFoundException_whenEmptyUser() {
        final String userName = "testUser";
        when(freelancerRepository.findByUserName(userName)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> freelancerService.findUserByUserName(userName));

        assertEquals("Wrong userName: No user found for the given userName: testUser", exception.getMessage());
        verify(freelancerRepository, times(1))
                .findByUserName(userName);
    }
}
