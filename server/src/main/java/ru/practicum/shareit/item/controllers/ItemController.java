package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {

    private final ItemService itemService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> save(@RequestHeader(HEADER) Long userId,
                                        @RequestBody ItemDto itemDto) {
        log.info("Запрос создания предмета от пользователя c id: {}", userId);
        return new ResponseEntity<>(itemService.save(userId, itemDto), HttpStatus.OK);
    }

    @PatchMapping(value = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestHeader(HEADER) Long userId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Запрос обновления предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return new ResponseEntity<>(itemService.update(itemId, userId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId,
                                               @RequestHeader(HEADER) Long userId) {
        log.info("Запрос на получение предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return new ResponseEntity<>(itemService.getById(itemId, userId), HttpStatus.OK);
    }

    @GetMapping
    public List<ItemDto> findAllByOwnerId(
            @RequestHeader(HEADER) Long ownerId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Запрос всех вщей от User ID {}.", ownerId);
        return itemService.findAllByOwnerId(ownerId, from, size);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> findAllByText(
            @RequestHeader(HEADER) Long ownerId,
            @RequestParam(name = "text") String text,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Запрос поиска по тексту. Text: {}.", text);
        return itemService.findAllByText(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDto> saveComment(
            @RequestHeader(HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария к предмету c id: {}, от пользователя c id: {}", itemId, userId);
        return new ResponseEntity<>(itemService.saveComment(userId, itemId, commentDto), HttpStatus.OK);
    }
}