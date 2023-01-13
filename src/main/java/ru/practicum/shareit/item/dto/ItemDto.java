package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class ItemDto {
    private long id;
    @NotBlank(message = "Не должно быть пустым")
    private String name;
    @NotBlank(message = "Не должно быть пустым")
    private String description;
    @BooleanFlag
    @NotNull(message = "Не должно быть пустым")
    private Boolean available;
}