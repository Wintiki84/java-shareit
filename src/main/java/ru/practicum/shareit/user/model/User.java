package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}

