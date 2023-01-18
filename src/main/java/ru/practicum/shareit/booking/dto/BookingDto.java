package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.validator.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDto {
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    @Future(groups = {Create.class})
    private LocalDateTime end;

    @NotNull
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
