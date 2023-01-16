package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDto {

    private Long id;

    // @NotNull(groups = {Create.class})
    //  @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    //  @NotNull(groups = {Create.class})
    //   @Future(groups = {Create.class})
    private LocalDateTime end;

    private Long itemId;
}
