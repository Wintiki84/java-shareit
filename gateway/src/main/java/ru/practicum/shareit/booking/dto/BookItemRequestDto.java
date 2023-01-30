package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid(groups = {Create.class})
public class BookItemRequestDto {

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    @Positive(message = "Должно быть больше нуля")
    private long itemId;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    @FutureOrPresent(groups = {Create.class}, message = "Должно быть в настоящем или будущем")
    private LocalDateTime start;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    @FutureOrPresent(groups = {Create.class}, message = "Должно быть в будущем")
    private LocalDateTime end;
}