package ru.practicum.shareit.request.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemRequestDto> save(
            @RequestHeader(HEADER) @Positive Long userId,
            @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос сохранения Request. User ID {}", userId);
        return new ResponseEntity<>(itemRequestService.save(userId, itemRequestDto), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findAll(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        log.info("Запрос на поиск всех запросов от User ID {}.", userId);
        return new ResponseEntity<>(itemRequestService.findAll(userId, from, size), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findAllByUserId(@RequestHeader("X-Sharer-User-Id")
                                                                @Positive Long userId) {
        log.info("Запрос на посиск всез запросво пользователя User ID {}.", userId);
        return new ResponseEntity<>(itemRequestService.findAllByUserId(userId), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> findById(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @PositiveOrZero Long requestId) {
        log.info("Запрос на поиск запроса request ID {} от User ID {}", requestId, userId);
        return new ResponseEntity<>(itemRequestService.findById(userId, requestId), HttpStatus.OK);
    }
}