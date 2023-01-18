package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByState(Long userId, String stateText) {
        if (Status.from(stateText) == null) {
            throw new IllegalArgumentException("Unknown state: " + stateText);
        }
        findByUserId(userId);

        switch (Status.valueOf(stateText)) {
            case CURRENT:
                return bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBefore(
                                userId,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findByBookerIdAndEndIsBefore(
                                userId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByBookerIdAndStartIsAfter(
                                userId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findByBookerIdAndStatus(
                                userId,
                                Status.WAITING,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findByBookerIdAndStatus(
                                userId,
                                Status.REJECTED,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository
                        .findByBookerId(
                                userId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByOwnerIdAndState(Long userId, String stateText) {
        if (Status.from(stateText) == null) {
            throw new IllegalArgumentException("Unknown state: " + stateText);
        }
        findByUserId(userId);
        List<BookingDto> bookings = bookingRepository.findByItemOwnerId(
                        userId,
                        Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new NotFoundException(String.format("Бронирований для пользователя ID %s не найдено", userId));
        }

        switch (Status.valueOf(stateText)) {
            case CURRENT:
                return bookingRepository
                        .findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                                userId,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findByItemOwnerIdAndEndIsBefore(
                                userId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByItemOwnerIdAndStartIsAfter(
                                userId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findByItemOwnerIdAndStatus(
                                userId,
                                Status.WAITING,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findByItemOwnerIdAndStatus(
                                userId,
                                Status.REJECTED,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookings;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = findByBookingId(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователь ID %s не создовал бронирование ID %s.",
                    userId, bookingId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto save(Long userId, BookingDto bookingDto) throws MissingResourceException {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new BookingException("Некорректное время бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(findByUserId(userId));
        booking.setItem(findByItemId(bookingDto.getItemId()));

        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свой товар.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new BookingException(String.format("Предмет ID %s недоступен", booking.getItem().getId()));
        }
        booking.setItem(booking.getItem());
        Booking bookingSave = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingSave);
    }

    @Override
    @Transactional
    public BookingDto updateState(Long userId, Long bookingId, Boolean approved) {
        if (approved == null) {
            throw new BookingException("Некорректный статус поддтверждения бронирования");
        }
        Booking booking = findByBookingId(bookingId);

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Только владелец может одобрить бронирование");
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
    public void delete(Long bookingId) {
        findByBookingId(bookingId);
        bookingRepository.deleteById(bookingId);
    }

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь ID %s не найден", userId)));
    }

    private Booking findByBookingId(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование ID %s не найдено", bookingId)));
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет ID %s не найден", itemId)));
    }
}