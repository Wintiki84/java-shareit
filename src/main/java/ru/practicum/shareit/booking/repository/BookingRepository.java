package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(
            Long userId,
            LocalDateTime timeEnd,
            LocalDateTime timeStart,
            Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByBookerId(Long userId, Sort sort);

    List<Booking> findByItemOwnerId(Long id, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
            Long userId,
            LocalDateTime timeEnd,
            LocalDateTime timeStart,
            Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long id, LocalDateTime time, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long id, Status status, Sort sort);

    List<Booking> findByBookerIdAndItemIdAndEndIsBefore(Long id, Long itemId, LocalDateTime time);

    @Query("SELECT b FROM  Booking b " +
            "WHERE b.item.id = :itemId AND b.item.owner.id = :userId AND b.status = 'APPROVED' AND b.end < :time " +
            "ORDER BY b.start DESC")
    Booking findLastBooking(LocalDateTime time, Long userId, Long itemId);

    @Query("SELECT b FROM  Booking b " +
            "WHERE b.item.id = :itemId AND b.item.owner.id = :userId AND b.status = 'APPROVED' AND b.start > :time " +
            "ORDER BY b.start")
    Booking findNextBooking(LocalDateTime time, Long userId, Long itemId);

    List<Booking> findAllByItemInAndStatus(List<Item> items, Status status, Sort sort);
}