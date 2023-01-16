package ru.practicum.shareit.item.model;

import jdk.jfr.BooleanFlag;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
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
    private User owner;
    private ItemRequest request;
}
