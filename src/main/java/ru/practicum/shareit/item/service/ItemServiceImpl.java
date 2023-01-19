package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ItemDto save(Long userId, ItemDto itemDto) {
        User user = findByUserId(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item itemSave = itemRepository.save(item);
        return ItemMapper.toItemDto(itemSave);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllUserItems(Long userId) {
        findByUserId(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        Map<Item, List<Comment>> commentsAll = commentRepository.findAllByItemIn(
                        items,
                        Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        Map<Item, List<Booking>> bookingsALL = bookingRepository.findAllByItemInAndStatus(
                        items,
                        Status.APPROVED,
                        Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<ItemDto> itemsDto = items
                .stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    List<Comment> comments = commentsAll.getOrDefault(item, Collections.emptyList());
                    List<Booking> bookings = bookingsALL.getOrDefault(item, Collections.emptyList());
                    LocalDateTime now = LocalDateTime.now();

                    Booking lastBooking = bookings.stream()
                            .filter(b -> ((b.getEnd().isEqual(now) || b.getEnd().isBefore(now))
                                    || (b.getStart().isEqual(now) || b.getStart().isBefore(now))))
                            .findFirst()
                            .orElse(null);

                    Booking nextBooking = bookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .reduce((first, second) -> second)
                            .orElse(null);

                    itemDto.setComments(comments
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(toList()));

                    itemDto.setLastBooking(lastBooking == null ? null : new ItemDto.ItemBooking(
                            lastBooking.getId(),
                            lastBooking.getBooker().getId()));

                    itemDto.setNextBooking(nextBooking == null ? null : new ItemDto.ItemBooking(
                            nextBooking.getId(),
                            nextBooking.getBooker().getId()));

                    return itemDto;
                })
                .collect(toList());

        return itemsDto;
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        findByUserId(userId);
        Item item = findByItemId(itemId);
        if (!item.getOwner().equals(userRepository.findById(userId).get())) {
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

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long userId) {
        findByUserId(userId);
        Item item = findByItemId(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        itemDto.setComments(commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList()));

        Booking lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), userId, itemId);
        Booking nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), userId, itemId);

        itemDto.setLastBooking(lastBooking == null ? null : new ItemDto.ItemBooking(
                lastBooking.getId(),
                lastBooking.getBooker().getId()));

        itemDto.setNextBooking(nextBooking == null ? null : new ItemDto.ItemBooking(
                nextBooking.getId(),
                nextBooking.getBooker().getId()));

        return itemDto;
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        findByUserId(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(toList());
        }
    }

    @Override
    @Transactional
    public CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = findByUserId(userId);
        Item item = findByItemId(itemId);

        boolean bookingApproved = bookingRepository
                .findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now())
                .stream()
                .noneMatch(booking -> booking.getStatus().equals(Status.APPROVED));
        if (bookingApproved) {
            throw new BookingException(String.format("Пользователю ID %s не было одобрено бронирование %s.",
                    userId, itemId));
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Comment commentSaveRepository = commentRepository.save(comment);
        return CommentMapper.toCommentDto(commentSaveRepository);
    }

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", userId)));
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет ID %s не найден", itemId)));
    }
}