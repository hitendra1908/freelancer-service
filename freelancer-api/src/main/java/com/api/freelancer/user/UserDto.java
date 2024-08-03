package com.api.freelancer.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
        @ApiModelProperty(required = true)
        String userName,

        @ApiModelProperty(required = true)
        String firstName,

        @ApiModelProperty(required = true)
        String lastName,

        @ApiModelProperty(required = true)
        String email) {

}

