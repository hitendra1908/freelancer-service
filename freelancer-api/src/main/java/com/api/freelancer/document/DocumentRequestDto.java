package com.api.freelancer.document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@ApiModel
@Data
@Builder
public class DocumentRequestDto {
        @ApiModelProperty(required = true, value = "type of the document")
        String documentType;

        @ApiModelProperty(required = true, value = "userName of the owner of the document")
        String userName;

        @ApiModelProperty(required = true, value = "expiry date of the document")
        LocalDate expiryDate;
}
