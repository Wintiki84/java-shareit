package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    List<Booking> findAllByState(Long userId, String stateText);

    List<Booking> findAllByOwnerIdAndState(Long userId, String stateText);

    Booking findById(Long userId, Long bookingId);

    Booking save(Long userId, BookingDto bookingDtoRequest);

    Booking updateState(Long userId, Long bookingId, Boolean approved);

    void delete(Long bookingId);
}