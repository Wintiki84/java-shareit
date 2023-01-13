package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}

