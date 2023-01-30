package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.pagination.Pagination;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @NotNull
    @Transactional
    public ItemDto save(@NotNull Long userId, @NotNull ItemDto itemDto) {
        User user = findByUserId(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Object itemDtoRequest;
        ItemRequest itemRequest = itemDto.getRequestId() == null ? null : itemRequestRepository
                .findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("ItemRequest не найден"));
        item.setRequest(itemRequest);
        Item itemSave = itemRepository.save(item);
        return ItemMapper.toItemDto(itemSave);
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllByOwnerId(Long ownerId, int from, int size) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, PageRequest.of(from, size));
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

        List<ItemDto> itemsDtoWithBookingList = items
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

        log.info("ItemService: findAllByOwnerId implementation. User ID {}.", ownerId);
        return itemsDtoWithBookingList;
    }

    @NotNull
    @Override
    @Transactional
    public ItemDto update(@NotNull Long itemId, @NotNull Long userId, @NotNull ItemDto itemDto) {
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

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(@NotNull Long itemId, @NotNull Long userId) {
        findByUserId(userId);
        Item item = findByItemId(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        LocalDateTime dateTime = LocalDateTime.now();

        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList()));

        Booking lastBooking = bookingRepository.findLastBooking(dateTime, userId, itemId,
                PageRequest.of(0, 1));
        Booking nextBooking = bookingRepository.findNextBooking(dateTime, userId, itemId,
                PageRequest.of(0, 1));

        itemDto.setLastBooking(lastBooking == null ? null : new ItemDto.ItemBooking(
                lastBooking.getId(),
                lastBooking.getBooker().getId()));

        itemDto.setNextBooking(nextBooking == null ? null : new ItemDto.ItemBooking(
                nextBooking.getId(),
                nextBooking.getBooker().getId()));

        log.info("ItemService: findById implementation. User ID {}, item ID {}.", userId, itemId);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllByText(String text, int from, int size) {
        log.info("ItemService: findAllByText implementation. Text: {}.", text);
        return itemRepository.search(text, PageRequest.of(from, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @NotNull
    @Override
    @Transactional
    public CommentDto saveComment(@NotNull Long userId, @NotNull Long itemId, @NotNull CommentDto commentDto) {
        User user = findByUserId(userId);
        Item item = findByItemId(itemId);

        boolean bookingBoolean = bookingRepository
                .findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now())
                .stream()
                .noneMatch(booking -> booking.getStatus().equals(Status.APPROVED));
        if (bookingBoolean) {
            throw new BookingException(String.format("User ID %s hasn't book item ID %s.", userId, itemId));
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Comment commentSave = commentRepository.save(comment);
        log.info("CommentService: save implementation. User ID {}, itemId {}.", userId, itemId);
        return CommentMapper.toCommentDto(commentSave);
    }

    @NotNull
    private User findByUserId(@NotNull Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", userId)));
    }

    @NotNull
    private Item findByItemId(@NotNull Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет ID %s не найден", itemId)));
    }
}