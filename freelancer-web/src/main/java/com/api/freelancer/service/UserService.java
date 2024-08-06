package com.api.freelancer.service;

import com.api.freelancer.document.DocumentResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DocumentsRepository documentsRepository;

    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::produceUserResponseDto)
                .toList();
    }

    public UserResponseDto save(UserRequestDto userRequestDto) {
        validateIncomingUser(userRequestDto);

        Users user = mapToUsers(userRequestDto);
        try {
            Users savedUser = userRepository.save(user);
            return produceUserResponseDto(savedUser);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateUserException("User already exists with username: "+ userRequestDto.userName());
        }
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        validateIncomingUser(userRequestDto);

        Users userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User you are trying to retrieve is not found"));

        if (!userToUpdate.getUserName().equals(userRequestDto.userName())) {
            throw new UserNameException("Updating username is not possible");
        }

        userToUpdate.setFirstName(userRequestDto.firstName());
        userToUpdate.setLastName(userRequestDto.lastName());
        userToUpdate.setEmail(userRequestDto.email());

        return produceUserResponseDto(userRepository.save(userToUpdate));
    }

    public UserResponseDto findUserById(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User you are trying to retrieve is not found"));

        return produceUserResponseDto(user);
    }

    private UserResponseDto produceUserResponseDto(Users user) {
        List<DocumentResponseDto> documentResponseDtoList = documentsRepository.findByUserUserName(user.getUserName()).stream()
                .map(this::mapToDocumentResponseDto)
                .toList();

        return UserResponseDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .documents(documentResponseDtoList)
                .build();
    }

    private DocumentResponseDto mapToDocumentResponseDto(Documents document) {
        return DocumentResponseDto.builder()
                .id(document.getId())
                .name(document.getName())
                .documentType(document.getDocumentType())
                .userName(document.getUser().getUserName())
                .fileType(document.getFileType())
                .expiryDate(document.getExpiryDate())
                .verified(document.isVerified())
                .build();
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
            throw new UserException("username should be at least 4 characters");
        }
        if (userRequestDto.firstName() == null || userRequestDto.firstName().isEmpty()) {
            throw new UserException("First name cannot be empty");
        }
        if (!EMAIL_REGEX_PATTERN.matcher(userRequestDto.email()).matches()) {
            throw new UserException("Invalid email address");
        }
    }
}
