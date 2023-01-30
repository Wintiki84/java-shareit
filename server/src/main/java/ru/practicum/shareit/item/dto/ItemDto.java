package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemOwner owner;
    private ItemBooking lastBooking;
    private ItemBooking nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

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