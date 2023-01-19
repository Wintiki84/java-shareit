package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.Create;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> findAllByState(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Запрос предметов по состоянию бронирования. User ID {}, state {}.", userId, state);
        return bookingService.findAllByState(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findAllByOwnerIdAndState(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateText) {
        log.info("Запрос бронирований для всех вещей текущего пользователя. User ID {}, state {}.", userId, stateText);
        return bookingService.findAllByOwnerIdAndState(userId, stateText);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDto findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        log.info("Запрос получения данных о бронировании. User ID {}, booking ID {}.", userId, bookingId);
        return bookingService.findById(userId, bookingId);
    }

    @PostMapping
    public BookingDto save(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        log.info("Запрос на добавление бронирования. User ID {}.", userId);
        return bookingService.save(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto updateState(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение или отклонение бронирования. User ID {}, booking ID {}.", userId, bookingId);
        return bookingService.updateState(userId, bookingId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Long bookingId) {
        log.info("Зпрос на Удаление бронирования. booking ID {}.", bookingId);
        bookingService.delete(bookingId);
    }
}