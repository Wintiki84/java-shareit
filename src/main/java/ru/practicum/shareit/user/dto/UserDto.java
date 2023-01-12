package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank(message = "Не должно быть пустым")
    private String name;
    @NotBlank(message = "Не должно быть пустым")
    @Email(message = "Неверный формат Email")
    private String email;
}
