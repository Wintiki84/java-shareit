package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@NotNull
public interface ItemRequestService {

    @NotNull
    List<ItemRequestDto> findAll(@NotNull Long userId, @NotNull Integer from, @NotNull Integer size);

    @NotNull
    List<ItemRequestDto> findAllByUserId(@NotNull Long userId);

    @NotNull
    ItemRequestDto findById(@NotNull Long userId, @NotNull Long requestId);

    @NotNull
    ItemRequestDto save(@NotNull Long userId, @NotNull ItemRequestDto itemRequestDto);
}
