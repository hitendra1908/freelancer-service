package com.api.freelancer.document;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

public record DocumentRequestDto(
        @ApiModelProperty(required = true)
        String documentType,
        @ApiModelProperty(required = true)
        String userName,
        @ApiModelProperty(required = true)
        LocalDate expiryDate
) {
}
