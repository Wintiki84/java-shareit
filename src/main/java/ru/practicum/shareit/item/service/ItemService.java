package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ItemService {

    @NotNull
    ItemDto save(@NotNull Long userId, @NotNull ItemDto itemDto);

    @NotNull
    List<ItemDto> getAllUserItems(@NotNull Long userId);

    @NotNull
    ItemDto update(@NotNull Long itemId, @NotNull Long userId, @NotNull ItemDto itemDto);

    @NotNull
    ItemDto getById(@NotNull Long itemId, @NotNull Long userId);

    @NotNull
    List<ItemDto> search(@NotNull Long userId, @NotNull String text);

    @NotNull
    CommentDto saveComment(@NotNull Long userId, @NotNull Long itemId, @NotNull CommentDto commentDto);
}