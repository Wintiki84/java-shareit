package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto save(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item itemSave = itemRepository.save(item);
        return ItemMapper.toItemDto(itemSave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllUserItems(Long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", itemId)));
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
        return ItemMapper.toItemDto(item);
    }

   /* @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllByText(String text) {
        log.info("ItemService: findAllByText implementation. Text: {}.", text);
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse findById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User ID %s doesn't exist.", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item ID %s doesn't exist.", itemId)));
        ItemDtoResponse itemDtoResponse = toItemDtoResponse(item);

        itemDtoResponse.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList()));

        Booking lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), userId, itemId);
        Booking nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), userId, itemId);

        itemDtoResponse.setLastBooking(lastBooking == null ? null : new ItemDtoResponse.ItemBooking(
                lastBooking.getId(),
                lastBooking.getBooker().getId()));

        itemDtoResponse.setNextBooking(nextBooking == null ? null : new ItemDtoResponse.ItemBooking(
                nextBooking.getId(),
                nextBooking.getBooker().getId()));

        log.info("ItemService: findById implementation. User ID {}, item ID {}.", userId, itemId);
        return itemDtoResponse;
    }*/

    @Override
    public ItemDto getById(long itemId, long userId) {
        userRepository.findById(userId);

        Item item = itemRepository.getById(itemId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        userRepository.findById(userId);

        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    /*@Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        findById(userId, itemId);
        log.info("ItemService: delete implementation. Item ID {}.", itemId);
        itemRepository.deleteById(itemId);
    }*/
}