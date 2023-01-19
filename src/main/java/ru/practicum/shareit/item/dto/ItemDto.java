package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private ItemOwner owner;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private List<CommentDto> comments;

    @Builder
    @Getter
    @Setter
    public static class ItemOwner {
        private final long id;
        private final String name;
    }

    @Data
    public static class ItemBooking {
        private final long id;
        private final long bookerId;
    }
}