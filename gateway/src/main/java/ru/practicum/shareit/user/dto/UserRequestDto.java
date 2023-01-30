package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String name;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    @Email(groups = {Create.class, Update.class}, message = "Неверный формат Email")
    private String email;
}
