package com.api.freelancer.service;

import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import com.api.freelancer.user.UserRequestDto;
import com.api.freelancer.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DocumentsRepository documentsRepository;

    public List<UserResponseDto> findAll() {
        List<Users> users =  userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .toList();
    }

    public UserResponseDto save(UserRequestDto userRequestDto) {
        validateIncomingUser(userRequestDto);
        final Users user = mapToUsers(userRequestDto);
        final Users savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    private UserResponseDto mapToUserDto(Users users) {

        List<Documents> documents = documentsRepository.findByUserUserName(users.getUserName());
        List<DocumentResponseDto> documentResponseDtoList = produceDocumentResponseDto(documents);

        return new UserResponseDto(users.getUserName(),
                users.getFirstName(),
                users.getLastName(),
                users.getEmail(),
                documentResponseDtoList
        );
    }

    private List<DocumentResponseDto> produceDocumentResponseDto(List<Documents> documents) {
        return documents.stream()
                .map(this::mapToDocumentResponseDto)
                .toList();
    }

    private DocumentResponseDto mapToDocumentResponseDto(Documents document) {
        return new DocumentResponseDto(document.getId(),
                document.getName(),
                document.getDocumentType(),
                document.getUser().getUserName(),
                document.getFileType(),
                document.getExpiryDate(),
                document.isVerified());
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

        if(userRequestDto.userName() == null || userRequestDto.userName().length() < 4) {
            throw new UserException("user name should be at least 4 chars");
        }
        if(userRequestDto.firstName() == null || userRequestDto.firstName().isEmpty()) {
            throw new UserException("first name can not be empty");
        }
        if(!validateEmailAddress(userRequestDto.email())) {
            throw new UserException("invalid email address");
        }
    }

    public boolean validateEmailAddress(String emailAddress) {
        final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
