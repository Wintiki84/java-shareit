package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @JsonView(Details.class)
    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive Integer size) {
        log.info("Запрос на поиск всех запросов от User ID {}.", userId);
        return itemRequestClient.getAll(userId, from, size);
    }

    @JsonView(Details.class)
    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@RequestHeader(HEADER) @Positive Long userId) {
        log.info("Запрос на посиск всез запросво пользователя User ID {}.", userId);
        return itemRequestClient.getItemRequestsByUser(userId);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("Запрос на поиск запроса request ID {} от User ID {}", requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @JsonView(Details.class)
    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(HEADER) @Positive Long userId,
            @Validated(Create.class)
            @RequestBody ItemRequestRequestDto requestDto) {
        log.info("Запрос сохранения Request. User ID {}", userId);
        return itemRequestClient.createItemRequest(userId, requestDto);
    }
}