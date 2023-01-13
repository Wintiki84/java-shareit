package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(userId));
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> getAllUserItems(long userId) {
        userRepository.findById(userId);
        return itemMapper.toListOfItemDto(itemRepository.findAllUserItems(userId));
    }

    @Override
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId);

        if (!item.getOwner().equals(userRepository.findById(userId))) {
            throw new NotFoundException(String.format("предмет с идентификатором: %d" +
                    " не принадлежит пользователю с идентификатором: %d", itemId, userId));
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        userRepository.findById(userId);

        Item item = itemRepository.findById(itemId);

        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        userRepository.findById(userId);

        return itemMapper.toListOfItemDto(itemRepository.search(text));
    }
}
