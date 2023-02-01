package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Item item;
    private Booker booker;
    private Status status;

    @Getter
    @Setter
    @Builder
    public static class Item {
        private final long id;
        private final String name;
    }

    @Getter
    @Setter
    @Builder
    public static class Booker {
        private final long id;
        private final String name;
    }
}
