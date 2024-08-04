package com.api.freelancer.service;

import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import com.api.freelancer.user.UserRequestDto;
import com.api.freelancer.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DocumentsRepository documentsRepository;
    private static final String EMAIL_REGEX_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    public UserResponseDto save(UserRequestDto userRequestDto) {
        validateIncomingUser(userRequestDto);
        Users user = mapToUsers(userRequestDto);
        Users savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    private UserResponseDto mapToUserDto(Users user) {
        List<DocumentResponseDto> documentResponseDtoList = documentsRepository.findByUserUserName(user.getUserName()).stream()
                .map(this::mapToDocumentResponseDto)
                .toList();

        return new UserResponseDto(
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                documentResponseDtoList
        );
    }

    private DocumentResponseDto mapToDocumentResponseDto(Documents document) {
        return new DocumentResponseDto(
                document.getId(),
                document.getName(),
                document.getDocumentType(),
                document.getUser().getUserName(),
                document.getFileType(),
                document.getExpiryDate(),
                document.isVerified()
        );
    }

    private Users mapToUsers(UserRequestDto userRequestDto) {
        return Users.builder()
                .userName(userRequestDto.userName())
                .firstName(userRequestDto.firstName())
                .lastName(userRequestDto.lastName())
                .email(userRequestDto.email())
                .build();
    }

    private void validateIncomingUser(UserRequestDto userRequestDto) {
        if (userRequestDto.userName() == null || userRequestDto.userName().length() < 4) {
            throw new UserException("user name should be at least 4 chars");
        }
        if (userRequestDto.firstName() == null || userRequestDto.firstName().isEmpty()) {
            throw new UserException("first name can not be empty");
        }
        if (!validateEmailAddress(userRequestDto.email())) {
            throw new UserException("invalid email address");
        }
    }

    private boolean validateEmailAddress(String emailAddress) {
        return Pattern.compile(EMAIL_REGEX_PATTERN)
                .matcher(emailAddress)
                .matches();
    }
}
