package com.api.freelancer.user;

import com.api.freelancer.document.DocumentResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDto(

        @ApiModelProperty(required = true, value = "username of the user")
        String userName,

        @ApiModelProperty(required = true, value = "first name of the user")
        String firstName,

        @ApiModelProperty(value = "last name of the user")
        String lastName,

        @ApiModelProperty(required = true, value = "emailId of the user")
        String email,

        @ApiModelProperty(value = "list of documents for user")
        List<DocumentResponseDto> documents) {
}
