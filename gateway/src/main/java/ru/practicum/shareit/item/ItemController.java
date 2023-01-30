package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @JsonView(Details.class)
    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(HEADER) Long ownerId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive Integer size) {
        log.info("Запрос всех вщей от User ID {}.", ownerId);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/search")
    public ResponseEntity<Object> getItemsByText(
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam(name = "text") String text,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive Integer size) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Запрос поиска по тексту. Text: {}.", text);
        return itemClient.getItemsByText(ownerId, text, from, size);
    }

    @JsonView(Details.class)
    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId) {
        log.info("Запрос на получение предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createItem(
            @RequestHeader(HEADER) @Positive Long userId,
            @Validated(Create.class)
            @RequestBody ItemDto requestDto) {
        log.info("Запрос создания предмета от пользователя c id: {}", userId);
        return itemClient.createItem(userId, requestDto);
    }

    @JsonView(Details.class)
    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @Validated(Create.class)
            @RequestBody CommentRequestDto commentDto) {
        log.info("Запрос на создание комментария к предмету c id: {}, от пользователя c id: {}", itemId, userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }

    @JsonView(Details.class)
    @PatchMapping(value = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateItem(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @Validated(Update.class)
            @RequestBody ItemDto requestDto) {
        log.info("Запрос обновления предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return itemClient.updateItem(requestDto, itemId, userId);
    }

    @JsonView(Details.class)
    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader(HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId) {
        log.info("Запрос на удаление предмета. User ID {}, itemId {}.", userId, itemId);
        return itemClient.deleteItem(itemId, userId);
    }
}