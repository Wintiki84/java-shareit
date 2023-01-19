package ru.practicum.shareit.booking.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;

    @JsonView(Details.class)
    @GetMapping
    public List<BookingDto> findAllByState(
            @RequestHeader("X-Sharer-User-Id") @Min(0) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Запрос предметов по состоянию бронирования. User ID {}, state {}.", userId, state);
        return bookingService.findAllByState(userId, state);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/owner")
    public List<BookingDto> findAllByOwnerIdAndState(
            @RequestHeader("X-Sharer-User-Id") @Min(0) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateText) {
        log.info("Запрос бронирований для всех вещей текущего пользователя. User ID {}, state {}.", userId, stateText);
        return bookingService.findAllByOwnerIdAndState(userId, stateText);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/{bookingId}")
    public BookingDto findById(
            @RequestHeader("X-Sharer-User-Id") @Min(0) Long userId,
            @PathVariable @Min(0) Long bookingId) {
        log.info("Запрос получения данных о бронировании. User ID {}, booking ID {}.", userId, bookingId);
        return bookingService.findById(userId, bookingId);
    }

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto save(
            @RequestHeader("X-Sharer-User-Id") @Min(0) Long userId,
            @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        log.info("Запрос на добавление бронирования. User ID {}.", userId);
        return bookingService.save(userId, bookingDto);
    }

    @JsonView(Details.class)
    @PatchMapping(value = "/{bookingId}")
    public BookingDto updateState(
            @RequestHeader("X-Sharer-User-Id") @Min(0) Long userId,
            @PathVariable @Min(0) Long bookingId,
            @RequestParam @NotNull Boolean approved) {
        log.info("Запрос на подтверждение или отклонение бронирования. User ID {}, booking ID {}.", userId, bookingId);
        return bookingService.updateState(userId, bookingId, approved);
    }

    @JsonView(Details.class)
    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable @Min(0) Long bookingId) {
        log.info("Зпрос на Удаление бронирования. booking ID {}.", bookingId);
        bookingService.delete(bookingId);
    }
}