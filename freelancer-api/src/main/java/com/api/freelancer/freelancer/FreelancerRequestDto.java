package com.api.freelancer.freelancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreelancerRequestDto {
        @ApiModelProperty(required = true, value = "username of the freelancer")
        String userName;

        @ApiModelProperty(required = true, value = "first name of the user")
        String firstName;

        @ApiModelProperty(value = "last name of the user")
        String lastName;

        @ApiModelProperty(required = true, value = "emailId of the user")
        String email;

}

