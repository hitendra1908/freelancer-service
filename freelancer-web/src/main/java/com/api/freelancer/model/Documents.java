package com.api.freelancer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Data
@Entity
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String documentType;

    @NotBlank
    private String fileType;

    //TODO check why its not working for findByUserName
/*    @NotBlank
    @Lob
    private byte[] content;*/

    @NotBlank
    private LocalDate expiryDate;

    private boolean verified;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
