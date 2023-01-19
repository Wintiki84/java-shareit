package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") long userId,
                        @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос создания предмета от пользователя c id: {}", userId);
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Запрос обновления предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение предмета c id: {}, от пользователя c id: {}", itemId, userId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение всех предметов от пользователя c id: {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam String text) {
        log.info("Запрос на получение предмета: {} от пользователя c id: {}", text, userId);
        return itemService.search(userId, text);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto saveComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария к предмету c id: {}, от пользователя c id: {}", itemId, userId);
        return itemService.saveComment(userId, itemId, commentDto);
    }
}