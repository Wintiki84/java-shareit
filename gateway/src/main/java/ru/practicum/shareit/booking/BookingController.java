package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @JsonView(Details.class)
    @GetMapping
    public ResponseEntity<Object> findAllByState(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateText,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive Integer size) {
        BookingState status = checkStatus(stateText);
        log.info("Запрос предметов по состоянию бронирования. User ID {}, state {}.", userId, status);
        return bookingClient.getBookingsByStatus(userId, status, from, size);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getBookingsByOwnerAndStatus(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateText,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive Integer size) {
        BookingState status = checkStatus(stateText);
        log.info("Запрос бронирований для всех вещей текущего пользователя. User ID {}, state {}.", userId, stateText);
        return bookingClient.getBookingsByOwnerAndStatus(userId, status, from, size);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId) {
        log.info("Запрос получения данных о бронировании. User ID {}, booking ID {}.", userId, bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER) @Positive Long userId,
            @Validated(Create.class)
            @RequestBody BookItemRequestDto bookingRequestDto) {
        log.info("Запрос на добавление бронирования. User ID {}.", userId);
        return bookingClient.createBooking(userId, bookingRequestDto);
    }

    @JsonView(Details.class)
    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam @NotNull Boolean approved) {
        log.info("Запрос на подтверждение или отклонение бронирования. User ID {}, booking ID {}.", userId, bookingId);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @JsonView(Details.class)
    @DeleteMapping(value = "/{bookingId}")
    public void delete(@PathVariable @Positive Long bookingId) {
        log.info("Зпрос на Удаление бронирования. booking ID {}.", bookingId);
        bookingClient.deleteBooking(bookingId);
    }

    private BookingState checkStatus(@NotBlank String stateText) {
        if (BookingState.from(stateText).isEmpty()) {
            throw new IllegalArgumentException("Unknown state: " + stateText);
        }
        return BookingState.valueOf(stateText);
    }
}

