package com.api.freelancer.document;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

public record DocumentRequestDto(
        @ApiModelProperty(required = true, value = "type of the document")
        String documentType,
        @ApiModelProperty(required = true, value = "userName of the owner of the document")
        String userName,
        @ApiModelProperty(required = true, value = "expiry date of the document")
        LocalDate expiryDate
) {
}
