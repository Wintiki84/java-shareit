package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controllers.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controllers.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controllers.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.controllers.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    private ItemDto itemDto;

    private ItemDto itemDto1;

    private UserDto userDto;

    private UserDto userDto1;

    private ItemRequestDto itemRequestDto;

    private CommentDto comment;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("test")
                .description("testDescription")
                .available(true)
                .build();

        itemDto1 = ItemDto.builder()
                .name("test1")
                .description("testDescription1")
                .available(true)
                .build();

        userDto = UserDto.builder().name("test").email("test@mail.com").build();

        userDto1 = UserDto.builder().name("test1").email("test1@mail.com").build();

        itemRequestDto = ItemRequestDto.builder().description("testDescription").build();

        comment = CommentDto.builder().text("text").build();
    }

    @Test
    void findAllByTextTest() {
        userController.save(userDto);
        itemController.save(1L, itemDto);
        assertEquals(1, itemController.search(1L, "Desc").getBody().size());
    }

    @Test
    void findAllByTextTestWithEmptyText() {
        userController.save(userDto);
        itemController.save(1L, itemDto);
        assertEquals(new ArrayList<ItemDto>(), itemController.search(1L, "").getBody());
    }

    @Test
    void findAllByTextTestWithWrongFrom() {
        assertThrows(NotFoundException.class, () -> itemController.search(-1, "t").getBody());
    }

    @Test
    void saveTest() {
        UserDto user = userController.save(userDto).getBody();
        ItemDto item = itemController.save(1L, itemDto).getBody();
        assertEquals(item.getId(), itemController.getItemById(item.getId(), user.getId()).getBody().getId());
    }

    @Test
    void saveTestWithRequest() {
        UserDto user = userController.save(userDto).getBody();
        itemRequestController.save(user.getId(), itemRequestDto);
        itemDto.setRequestId(1L);
        userController.save(userDto1);
        ItemDto item = itemController.save(2L, itemDto).getBody();
        assertEquals(item.getId(), itemController.getItemById(1L, 1L).getBody().getId());
    }

    @Test
    void saveTestWithWrongUserId() {
        assertThrows(NotFoundException.class, () -> itemController.save(1L, itemDto));
    }

    @Test
    void saveTestWithWrongItemRequest() {
        itemDto.setRequestId(10L);
        userController.save(userDto);
        assertThrows(NotFoundException.class, () -> itemController.save(1L, itemDto));
    }

    @Test
    void saveCommentTest() throws InterruptedException {
        UserDto user = userController.save(userDto).getBody();
        ItemDto item = itemController.save(user.getId(), itemDto).getBody();
        UserDto user1 = userController.save(userDto1).getBody();
        bookingController.save(user1.getId(), BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .itemId(item.getId())
                .build());

        bookingController.updateState(1L, user.getId(), true);
        TimeUnit.SECONDS.sleep(2);
        itemController.saveComment(user1.getId(), item.getId(), comment);
        assertEquals(0, itemController.search(user1.getId(), "test").getBody().get(0).getComments().size());
    }

    @Test
    void saveCommentTestWithWrongUserId() {
        assertThrows(NotFoundException.class, () -> itemController.saveComment(1L, 1L, comment));
    }

    @Test
    void saveCommentTestWithWrongItemId() {
        userController.save(userDto);
        assertThrows(NotFoundException.class, () -> itemController.saveComment(1L, 1L, comment));
        itemController.save(1L, itemDto);
        assertThrows(BookingException.class, () -> itemController.saveComment(1L, 1L, comment));
    }

    @Test
    void updateTest() {
        userController.save(userDto);
        itemController.save(1L, itemDto);
        itemController.update(1L, 1L, itemDto1);
        assertEquals(itemDto1.getDescription(), itemController.search(1L, "test").getBody().get(0)
                .getDescription());
    }

    @Test
    void updateTestWithWrongItemId() {
        assertThrows(NotFoundException.class, () -> itemController.update(1L, 1L, itemDto));
    }

    @Test
    void updateTestWithWrongUserId() {
        userController.save(userDto);
        itemController.save(1L, itemDto);
        assertThrows(NotFoundException.class, () -> itemController.update(10L, 1L, itemDto1));
    }
}
