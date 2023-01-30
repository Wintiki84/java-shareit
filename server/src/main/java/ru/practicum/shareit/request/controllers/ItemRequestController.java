package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemRequestDto> save(
            @RequestHeader(HEADER) Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос сохранения Request. User ID {}", userId);
        return new ResponseEntity<>(itemRequestService.save(userId, itemRequestDto), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findAll(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            Integer size) {
        log.info("Запрос на поиск всех запросов от User ID {}.", userId);
        return new ResponseEntity<>(itemRequestService.findAll(userId, from, size), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findAllByUserId(@RequestHeader(HEADER)
                                                                Long userId) {
        log.info("Запрос на посиск всез запросво пользователя User ID {}.", userId);
        return new ResponseEntity<>(itemRequestService.findAllByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> findById(
            @RequestHeader(HEADER) Long userId,
            @PathVariable Long requestId) {
        log.info("Запрос на поиск запроса request ID {} от User ID {}", requestId, userId);
        return new ResponseEntity<>(itemRequestService.findById(userId, requestId), HttpStatus.OK);
    }
}
