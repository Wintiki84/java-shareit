package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private long id;
    private Date start;
    private Date end;
    private Item item;
    private User booker;
}
