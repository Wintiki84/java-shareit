package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDto {

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    @Min(value = 0, message = "Должно быть больше нуля")
    private Long id;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class})
    @Future(groups = {Create.class})
    private LocalDateTime end;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class})
    @Min(value = 0, message = "Должно быть больше нуля")
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
