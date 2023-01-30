package ru.practicum.shareit.item.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {

    private final ItemService itemService;

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> save(@RequestHeader(HEADER) @Positive long userId,
                                        @Validated(Create.class) @RequestBody @Positive ItemDto itemDto) {
        log.info("Запрос создания предмета от пользователя c id: {}", userId);
        return new ResponseEntity<>(itemService.save(userId, itemDto), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @PatchMapping(value = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> update(@PathVariable @Positive long itemId,
                                          @RequestHeader(HEADER) @Positive long userId,
                                          @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос обновления предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return new ResponseEntity<>(itemService.update(itemId, userId, itemDto), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable @Positive long itemId,
                                               @RequestHeader(HEADER) @Positive long userId) {
        log.info("Запрос на получение предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return new ResponseEntity<>(itemService.getById(itemId, userId), HttpStatus.OK);
    }

    @GetMapping
    public List<ItemDto> findAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("ItemServerController: findAllByOwnerId implementation. User ID {}.", ownerId);
        return itemService.findAllByOwnerId(ownerId, from, size);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> findAllByText(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "text") String text,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("ItemServerController: findAllByText implementation. Text: {}.", text);
        return itemService.findAllByText(text, from, size);
    }

    @JsonView(Details.class)
    @PostMapping(value = "/{itemId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDto> saveComment(
            @RequestHeader(HEADER) @Positive long userId,
            @PathVariable @Positive long itemId,
            @Validated(Create.class) @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария к предмету c id: {}, от пользователя c id: {}", itemId, userId);
        return new ResponseEntity<>(itemService.saveComment(userId, itemId, commentDto), HttpStatus.OK);
    }
}