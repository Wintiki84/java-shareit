package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

@Builder
@Getter
@Setter
public class UserDto {

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    @Positive(message = "Должно быть больше нуля")
    private Long id;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String name;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    @Email(groups = {Create.class, Update.class}, message = "Неверный формат Email")
    private String email;
}
