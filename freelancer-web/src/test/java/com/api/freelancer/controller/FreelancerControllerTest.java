package com.api.freelancer.controller;

import com.api.freelancer.service.FreelancerService;
import com.api.freelancer.freelancer.FreelancerRequestDto;
import com.api.freelancer.freelancer.FreelancerResponseDto;
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
public class FreelancerControllerTest {

    @InjectMocks
    private FreelancerController freelancerController;

    @Mock
    private FreelancerService freelancerService;

    private FreelancerRequestDto freelancerRequestDto;
    private FreelancerResponseDto user1;
    private FreelancerResponseDto user2;

    @BeforeEach
    void setup() {
        freelancerRequestDto = FreelancerRequestDto.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        user1 = FreelancerResponseDto.builder()
                .id(1L)
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .documents(null)
                .build();
        user1 = FreelancerResponseDto.builder()
                .id(2L)
                .userName("batman")
                .firstName("Bruce")
                .lastName("Wayne")
                .email("bruce.wayne@example.com")
                .documents(null)
                .build();
    }

    @Test
    public void shouldGetAllFreelancers() {
        List<FreelancerResponseDto> expectedUsers = Arrays.asList(user1, user2);

        when(freelancerService.findAll()).thenReturn(expectedUsers);

        ResponseEntity<List<FreelancerResponseDto>> actualUsers = freelancerController.getAllFreelancers();

        assertEquals(ResponseEntity.ok(expectedUsers), actualUsers);
        verify(freelancerService, times(1)).findAll();

    }

    @Test
    public void shouldSaveAndReturnUser() {

        when(freelancerService.save(freelancerRequestDto)).thenReturn(user1);

        ResponseEntity<FreelancerResponseDto> actualUser = freelancerController.addFreelancer(freelancerRequestDto);

        assertEquals(ResponseEntity.ok(user1), actualUser);
        verify(freelancerService, times(1)).save(freelancerRequestDto);
    }

    @Test
    public void shouldUpdateAndReturnUser() {
        when(freelancerService.updateUser(1L, freelancerRequestDto)).thenReturn(user1);

        ResponseEntity<FreelancerResponseDto> actualUser = freelancerController.updateFreelancer(1L, freelancerRequestDto);

        assertEquals(ResponseEntity.ok(user1), actualUser);
        verify(freelancerService, times(1)).updateUser(1L, freelancerRequestDto);
    }

    @Test
    public void shouldReturnUser_WhenIdIsPassed() {
        when(freelancerService.findUserById(1L)).thenReturn(user1);

        ResponseEntity<FreelancerResponseDto> actualUser = freelancerController.getFreelancerById(1L);

        assertEquals(ResponseEntity.ok(user1), actualUser);
        verify(freelancerService, times(1)).findUserById(1L);

    }
}
