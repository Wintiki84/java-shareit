package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controllers.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controllers.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controllers.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTest {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private UserDto userDto;

    private UserDto userDto1;

    private BookingDto bookingDto;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("testItem")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();

        userDto1 = UserDto.builder()
                .id(2L)
                .name("test1")
                .email("test1@mail.ru")
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10))
                .booker(BookingDto.Booker.builder()
                        .id(userDto.getId())
                        .name(userDto.getName())
                        .build())
                .item(BookingDto.Item.builder()
                        .id(itemDto.getId())
                        .name(itemDto.getName())
                        .build())
                .build();
    }

    @Test
    void saveTest() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        ResponseEntity<BookingDto> booking = bookingController.save(user1.getBody().getId(), bookingDto);
        assertEquals(1L, bookingController.findById(user1.getBody().getId(), booking.getBody().getId())
                .getBody().getId());
    }

    @Test
    void saveTestWithWrongUser() {
        assertThrows(NotFoundException.class,
                () -> bookingController.save(1L, bookingDto));
    }

    @Test
    void saveTestWithWrongItem() {
        bookingDto.setItemId(30L);
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        assertThrows(NotFoundException.class,
                () -> bookingController.save(user1.getBody().getId(), bookingDto));
    }

    @Test
    void saveTestWithOwner() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        assertThrows(NotFoundException.class,
                () -> bookingController.save(user.getBody().getId(), bookingDto));
    }

    @Test
    void saveTestWithUnavailableItem() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        itemDto.setAvailable(false);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        assertThrows(BookingException.class,
                () -> bookingController.save(user1.getBody().getId(), bookingDto));
    }

    @Test
    void updateStateTest() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        ResponseEntity<BookingDto> booking = bookingController.save(user1.getBody().getId(), bookingDto);
        assertEquals(Status.WAITING, bookingController.findById(user1.getBody().getId(),
                booking.getBody().getId()).getBody().getStatus());
        bookingController.updateState(user.getBody().getId(), booking.getBody().getId(), true);
        assertEquals(Status.APPROVED, bookingController.findById(user1.getBody().getId(),
                booking.getBody().getId()).getBody().getStatus());
    }

    @Test
    void updateStatusTestWithWrongBooking() {
        assertThrows(NotFoundException.class,
                () -> bookingController.updateState(1L, 1L, true));
    }

    @Test
    void updateStatusTestWithWrongUser() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        bookingController.save(user1.getBody().getId(), bookingDto);
        assertThrows(NotFoundException.class,
                () -> bookingController.updateState(1L, 2L, true));
    }

    @Test
    void updateStatusTestWithWrongStatus() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        bookingController.save(user1.getBody().getId(), bookingDto);
        bookingController.updateState(1L, 1L, true);
        assertThrows(BookingException.class,
                () -> bookingController.updateState(1L, 1L, true));
    }

    @Test
    void findAllByUserTest() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        ResponseEntity<BookingDto> booking = bookingController.save(user1.getBody().getId(), bookingDto);
        assertEquals(1, bookingController
                .findAllByState(user1.getBody().getId(), "WAITING", 0, 10).getBody().size());
        assertEquals(1, bookingController
                .findAllByState(user1.getBody().getId(), "ALL", 0, 10).getBody().size());
        assertEquals(0, bookingController
                .findAllByState(user1.getBody().getId(), "PAST", 0, 10).getBody().size());
        assertEquals(0, bookingController
                .findAllByState(user1.getBody().getId(), "CURRENT", 0, 10).getBody().size());
        assertEquals(1, bookingController
                .findAllByState(user1.getBody().getId(), "FUTURE", 0, 10).getBody().size());
        assertEquals(0, bookingController
                .findAllByState(user1.getBody().getId(), "REJECTED", 0, 10).getBody().size());
        bookingController.updateState(booking.getBody().getId(), user.getBody().getId(), true);
        assertEquals(0, bookingController
                .findAllByState(user.getBody().getId(), "CURRENT", 0, 10).getBody().size());
        assertEquals(1, bookingController
                .findAllByOwnerIdAndState(user.getBody().getId(), "ALL", 0, 10).getBody().size());
        assertEquals(0, bookingController
                .findAllByOwnerIdAndState(user.getBody().getId(), "WAITING", 0, 10)
                .getBody().size());
        assertEquals(1, bookingController
                .findAllByOwnerIdAndState(user.getBody().getId(), "FUTURE", 0, 10).getBody().size());
        assertEquals(0, bookingController
                .findAllByOwnerIdAndState(user.getBody().getId(), "REJECTED", 0, 10)
                .getBody().size());
        assertEquals(0, bookingController
                .findAllByOwnerIdAndState(user.getBody().getId(), "PAST", 0, 10).getBody().size());
    }

    @Test
    void findAllTestsWithWrongUserId() {
        assertThrows(NotFoundException.class, () -> bookingController
                .findAllByState(1L, "ALL", 0, 10));
        assertThrows(NotFoundException.class, () -> bookingController
                .findAllByOwnerIdAndState(1L, "ALL", 0, 10));
    }

   // @Test
   // void findAllTestsWithWrongForm() {
   //     assertThrows(ConstraintViolationException.class, () -> bookingController
   //             .findAllByState(1L, "ALL", -1, 10));
   //     assertThrows(ConstraintViolationException.class, () -> bookingController
   //             .findAllByOwnerIdAndState(1L, "ALL", -1, 10));
   // }

    @Test
    void findByIdTestWithWrongId() {
        assertThrows(NotFoundException.class,
                () -> bookingController.findById(1L, 1L));
    }

    @Test
    void findByWrongUserTest() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        bookingController.save(user1.getBody().getId(), bookingDto);
        assertThrows(NotFoundException.class, () -> bookingController.findById(1L, 10L));
    }

    @Test
    void deleteTest() {
        ResponseEntity<UserDto> user = userController.save(userDto);
        ResponseEntity<ItemDto> item = itemController.save(user.getBody().getId(), itemDto);
        bookingDto.setItemId(item.getBody().getId());
        ResponseEntity<UserDto> user1 = userController.save(userDto1);
        ResponseEntity<BookingDto> booking = bookingController.save(user1.getBody().getId(), bookingDto);
        assertEquals(1L, bookingController.findById(user.getBody().getId(),
                booking.getBody().getId()).getBody().getId());
        bookingController.delete(booking.getBody().getId());
        assertThrows(NotFoundException.class, () -> bookingController.findById(user.getBody().getId(),
                booking.getBody().getId()));
    }
}