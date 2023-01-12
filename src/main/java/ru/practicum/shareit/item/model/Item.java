package ru.practicum.shareit.item.model;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Item {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @BooleanFlag
    @NotBlank
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;
}
