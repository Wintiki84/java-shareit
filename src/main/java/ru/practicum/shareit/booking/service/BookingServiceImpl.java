package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByState(@NotNull Long userId, @NotNull String stateText, @NotNull Integer from
            , @NotNull Integer size) {

        findByUserId(userId);
        Pageable pageable = createPageRequest(from, size);

        switch (checkStatus(stateText)) {
            case CURRENT:
                return bookingRepository
                        .findByBookerIdAndCurrentOrderByStartDesc(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByStartDesc(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(
                                userId,
                                Status.WAITING,
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(
                                userId,
                                Status.REJECTED,
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository
                        .findByBookerIdOrderByStartDesc(
                                userId,
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByOwnerIdAndState(@NotNull Long userId, @NotNull String stateText,
                                                     @NotNull Integer from, @NotNull Integer size) {

        findByUserId(userId);
        Pageable pageable = createPageRequest(from, size);

        List<BookingDto> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(
                        userId,
                        pageable)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new NotFoundException(String.format("Бронирований для пользователя ID %s не найдено", userId));
        }

        switch (checkStatus(stateText)) {
            case CURRENT:
                return bookingRepository
                        .findByItemOwnerIdAndCurrentOrderByStartDesc(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(
                                userId,
                                Status.WAITING,
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(
                                userId,
                                Status.REJECTED,
                                pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookings;
        }
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(@NotNull Long userId, @NotNull Long bookingId) {
        Booking booking = findByBookingId(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователь ID %s не создовал бронирование ID %s.",
                    userId, bookingId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @NotNull
    @Override
    @Transactional
    public BookingDto save(@NotNull Long userId, @NotNull BookingDto bookingDto) throws MissingResourceException {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new BookingException("BookingService.save: Некорректное время бронирования: окончание бронирования " +
                    bookingDto.getEnd() + " позже начала " + bookingDto.getStart().toString());
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(findByUserId(userId));
        booking.setItem(findByItemId(bookingDto.getItemId()));

        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Владелец ID %s не может забронировать свой товар", userId));
        }
        if (!booking.getItem().getAvailable()) {
            throw new BookingException(String.format("Предмет ID %s недоступен", booking.getItem().getId()));
        }
        booking.setItem(booking.getItem());
        Booking bookingSave = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingSave);
    }

    @NotNull
    @Override
    @Transactional
    public BookingDto updateState(@NotNull Long userId, @NotNull Long bookingId, @NotNull Boolean approved) {
        if (approved == null) {
            throw new BookingException("Некорректный статус NULL поддтверждения бронирования");
        }
        Booking booking = findByBookingId(bookingId);

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(String.format("Только владелец ID %s может одобрить бронирование",
                    booking.getItem().getOwner().getId()));
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingException("Бронирование уже одобрено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public void delete(@NotNull Long bookingId) {
        findByBookingId(bookingId);
        bookingRepository.deleteById(bookingId);
    }

    @NotNull
    private User findByUserId(@NotNull Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", userId)));
    }

    @NotNull
    private Booking findByBookingId(@NotNull Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование ID %s не найдено", bookingId)));
    }

    @NotNull
    private Item findByItemId(@NotNull Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет ID %s не найден", itemId)));
    }

    private PageRequest createPageRequest(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }

    private Status checkStatus(String stateText) {
        if (Status.from(stateText) == null) {
            log.info("BookingServiceImpl.findAllByOwnerIdAndState: Неизвестное состояние" + stateText);
            //С сообщением об ошибке на русском не проходит тесты
            throw new IllegalArgumentException("Unknown state: " + stateText);
        }
        return Status.valueOf(stateText);
    }
}