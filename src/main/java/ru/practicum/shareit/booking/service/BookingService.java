package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.MissingResourceException;

public interface BookingService {

    @NotNull
    List<BookingDto> findAllByState(@NotNull Long userId, @NotNull String status, @NotNull Integer from
            , @NotNull Integer size);

    @NotNull
    List<BookingDto> findAllByOwnerIdAndState(@NotNull Long userId, @NotNull String status, @NotNull Integer from
            , @NotNull Integer size);

    @NotNull
    BookingDto findById(@NotNull Long userId, @NotNull Long bookingId);

    @NotNull
    BookingDto save(@NotNull Long userId, @NotNull BookingDto bookingDto) throws MissingResourceException;

    @NotNull
    BookingDto updateState(@NotNull Long userId, @NotNull Long bookingId, @NotNull Boolean approved);

    void delete(@NotNull Long bookingId);
}