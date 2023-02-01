package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String name;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String description;

    @BooleanFlag
    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    private Boolean available;

    @JsonView({Details.class, AdminDetails.class})
    private Long requestId;
}
