package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;
import java.util.MissingResourceException;

public interface BookingService {

    List<BookingDto> findAllByState(Long userId, String stateText);

    List<BookingDto> findAllByOwnerIdAndState(Long userId, String stateText);

    BookingDto findById(Long userId, Long bookingId);

    BookingDto save(Long userId, BookingDto bookingDto) throws MissingResourceException;

    BookingDto updateState(Long userId, Long bookingId, Boolean approved);

    void delete(Long bookingId);
}