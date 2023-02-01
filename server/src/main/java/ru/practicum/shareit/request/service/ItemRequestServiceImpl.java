package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAll(@NotNull Long userId, @NotNull Integer from, @NotNull Integer size) {
        findByUserId(userId);

        PageRequest pageRequest = createPageRequest(from, size);

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNotLike(userId, pageRequest);
        return getItemRequestDtoList(itemRequests);
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        findByUserId(userId);

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedAsc(userId);
        return getItemRequestDtoList(itemRequests);
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findById(@NotNull Long userId, @NotNull Long requestId) {
        findByUserId(userId);
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрошенный запрос %s не существует",
                        requestId)));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequest.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList()));
        return itemRequestDto;
    }

    @NotNull
    @Override
    @Transactional
    public ItemRequestDto save(@NotNull Long userId, @NotNull ItemRequestDto itemRequestDto) {
        User user = findByUserId(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @NotNull
    private List<ItemRequestDto> getItemRequestDtoList(@NotNull List<ItemRequest> itemRequests) {

        Map<ItemRequest, List<Item>> itemsAll = itemRepository
                .findAllByRequestIn(itemRequests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));

        return itemRequests
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    List<Item> itemList = itemsAll.getOrDefault(itemRequest, Collections.emptyList());
                    itemRequestDto.setItems(itemList
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(toList()));
                    return itemRequestDto;
                })
                .collect(toList());
    }

    @NotNull
    private PageRequest createPageRequest(@NotNull Integer from, @NotNull Integer size) {
        return PageRequest.of(from / size, size);
    }

    @NotNull
    private User findByUserId(@NotNull Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", userId)));
    }
}
