package com.api.freelancer.document;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@ApiModel
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentResponseDto {
        @ApiModelProperty(required = true, value = "unique id of the document")
        Long id;

        @ApiModelProperty(required = true, value = "name of the document")
        String name;

        @ApiModelProperty(required = true, value = "type of the document")
        String documentType;

        @ApiModelProperty(required = true, value = "owner of the document")
        String userName;

        @ApiModelProperty(required = true, value = "type of the file eg: pdf, jpeg")
        String fileType;

        @ApiModelProperty(required = true, value = "expire date of the document")
        LocalDate expiryDate;

        @ApiModelProperty(required = true)
        boolean verified;
}
