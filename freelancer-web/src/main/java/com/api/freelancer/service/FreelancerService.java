package com.api.freelancer.service;

import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.freelancer.DuplicateUserException;
import com.api.freelancer.exception.freelancer.UserException;
import com.api.freelancer.exception.freelancer.UserNameException;
import com.api.freelancer.exception.freelancer.UserNotFoundException;
import com.api.freelancer.entity.Documents;
import com.api.freelancer.entity.Freelancer;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.FreelancerRepository;
import com.api.freelancer.freelancer.FreelancerRequestDto;
import com.api.freelancer.freelancer.FreelancerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreelancerService {

    private final FreelancerRepository freelancerRepository;
    private final DocumentsRepository documentsRepository;

    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    public List<FreelancerResponseDto> findAll() {
        return freelancerRepository.findAll().stream()
                .map(this::produceFreelancerResponseDto)
                .toList();
    }

    public FreelancerResponseDto save(FreelancerRequestDto freelancerRequestDto) {
        validateIncomingUser(freelancerRequestDto);

        Freelancer freelancer = consumeToFreelancer(freelancerRequestDto);
        try {
            Freelancer savedFreelancer = freelancerRepository.save(freelancer);
            return produceFreelancerResponseDto(savedFreelancer);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateUserException("User already exists with username: "+ freelancerRequestDto.getUserName());
        }
    }

    public FreelancerResponseDto updateUser(Long id, FreelancerRequestDto freelancerRequestDto) {
        validateIncomingUser(freelancerRequestDto);

        Freelancer freelancerToUpdate = freelancerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User you are trying to update is not found"));

        if (!freelancerToUpdate.getUserName().equals(freelancerRequestDto.getUserName())) {
            throw new UserNameException("Changing username is not allowed");
        }

        freelancerToUpdate.setFirstName(freelancerRequestDto.getFirstName());
        freelancerToUpdate.setLastName(freelancerRequestDto.getLastName());
        freelancerToUpdate.setEmail(freelancerRequestDto.getEmail());

        return produceFreelancerResponseDto(freelancerRepository.save(freelancerToUpdate));
    }

    public FreelancerResponseDto findUserById(Long id) {
        Freelancer freelancer = freelancerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User you are trying to retrieve is not found"));

        return produceFreelancerResponseDto(freelancer);
    }

    public Freelancer findUserByUserName (final String userName) {
        return freelancerRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException(
                        "Wrong userName: No user found for the given userName: " + userName));
    }

    private FreelancerResponseDto produceFreelancerResponseDto(Freelancer freelancer) {
        List<DocumentResponseDto> documentResponseDtoList = documentsRepository.findByFreelancerUserName(freelancer.getUserName()).stream()
                .map(this::produceDocumentResponseDto)
                .toList();

        return FreelancerResponseDto.builder()
                .id(freelancer.getId())
                .userName(freelancer.getUserName())
                .firstName(freelancer.getFirstName())
                .lastName(freelancer.getLastName())
                .email(freelancer.getEmail())
                .documents(documentResponseDtoList)
                .build();
    }

    private DocumentResponseDto produceDocumentResponseDto(Documents document) {
        return DocumentResponseDto.builder()
                .id(document.getId())
                .name(document.getName())
                .documentType(document.getDocumentType())
                .userName(document.getFreelancer().getUserName())
                .fileType(document.getFileType())
                .expiryDate(document.getExpiryDate())
                .verified(document.isVerified())
                .build();
    }

    private Freelancer consumeToFreelancer(FreelancerRequestDto freelancerRequestDto) {
        return Freelancer.builder()
                .userName(freelancerRequestDto.getUserName())
                .firstName(freelancerRequestDto.getFirstName())
                .lastName(freelancerRequestDto.getLastName())
                .email(freelancerRequestDto.getEmail())
                .build();
    }

    private void validateIncomingUser(FreelancerRequestDto freelancerRequestDto) {
        if (freelancerRequestDto.getUserName() == null || freelancerRequestDto.getUserName().length() < 4) {
            log.error("error occurred while validating username");
            throw new UserException("username should be at least 4 characters");
        }
        if (freelancerRequestDto.getFirstName() == null || freelancerRequestDto.getFirstName().isEmpty()) {
            log.error("error occurred while validating user first name");
            throw new UserException("First name cannot be empty");
        }
        if (!EMAIL_REGEX_PATTERN.matcher(freelancerRequestDto.getEmail()).matches()) {
            log.error("error occurred while validating user's email address");
            throw new UserException("Invalid email address");
        }
    }
}
