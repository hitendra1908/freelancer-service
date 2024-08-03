package com.api.freelancer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Users {
    @Id
//    @NotBlank //TODO check this validation
//    @Size(min = 3, max = 50)
    private String userName;

   /* @NotBlank
    @Size(min = 2, max = 50)*/ //TODO check this validation
    private String firstName;

//    @NotBlank
//    @Size(min = 2, max = 50) //TODO check this validation
    private String lastName;

//    @Email
//    @NotBlank //TODO check this validation jakarta validation
    private String email;
}

