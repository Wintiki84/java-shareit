package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAllByState(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0")
            Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            Integer size) {
        log.info("Запрос предметов по состоянию бронирования. User ID {}, state {}.", userId, state);
        return new ResponseEntity<>(bookingService.findAllByState(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<List<BookingDto>> findAllByOwnerIdAndState(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateText,
            @RequestParam(value = "from", defaultValue = "0")
            Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            Integer size) {
        log.info("Запрос бронирований для всех вещей текущего пользователя. User ID {}, state {}.", userId, stateText);
        return new ResponseEntity<>(bookingService.findAllByOwnerIdAndState(userId, stateText, from, size),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<BookingDto> findById(
            @RequestHeader(HEADER) Long userId,
            @PathVariable Long bookingId) {
        log.info("Запрос получения данных о бронировании. User ID {}, booking ID {}.", userId, bookingId);
        return new ResponseEntity<>(bookingService.findById(userId, bookingId), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingDto> save(
            @RequestHeader(HEADER) Long userId,
            @RequestBody BookingDto bookingDto) {
        log.info("Запрос на добавление бронирования. User ID {}.", userId);
        return new ResponseEntity<>(bookingService.save(userId, bookingDto), HttpStatus.OK);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<BookingDto> updateState(
            @RequestHeader(HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение или отклонение бронирования. User ID {}, booking ID {}.", userId, bookingId);
        return new ResponseEntity<>(bookingService.updateState(userId, bookingId, approved), HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long bookingId) {
        log.info("Зпрос на Удаление бронирования. booking ID {}.", bookingId);
        bookingService.delete(bookingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}