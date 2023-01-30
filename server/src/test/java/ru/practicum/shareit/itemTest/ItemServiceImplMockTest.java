package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class ItemServiceImplMockTest {

    ItemService itemService;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository
        );
    }

    @Test
    void saveCommentTest() {
        CommentDto commentDto = CommentDto.builder().text("text").build();

        CommentDto commentInfoDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("test")
                .created(LocalDateTime.now())
                .build();

        User user = buildUser();

        Item item = buildItem();
        item.setId(1L);

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        Booking booking = Booking.builder().status(Status.APPROVED).build();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);

        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        CommentDto foundComment = itemService.saveComment(1L, 1L, commentDto);

        Assertions.assertNotNull(foundComment);
        Assertions.assertEquals(commentInfoDto.getId(), foundComment.getId());
        Assertions.assertEquals(commentInfoDto.getText(), foundComment.getText());
        Assertions.assertEquals(commentInfoDto.getAuthorName(), foundComment.getAuthorName());
    }


    @Test
    void saveTest() {
        User user = buildUser();

        Item item = buildItem();
        item.setId(1L);
        item.setOwner(user);

        ItemDto itemDto = buildItemDto();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto foundItem = itemService.save(user.getId(), itemDto);

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(item.getId(), foundItem.getId());
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void saveTestWithWrongUserId() {
        ItemDto itemDto = buildItemDto();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.save(1L, itemDto));

        Assertions.assertEquals("Пользователь ID 1 не найден", exception.getMessage());
    }

    @Test
    void saveTestWithRequest() {
        User user1 = buildUser();

        User user2 = buildUser();
        user2.setId(2L);
        user2.setName("test1");
        user2.setEmail("test2@mail.com");

        ItemRequest itemRequest =
                new ItemRequest(1L, "testDescription", user2, LocalDateTime.now());

        Item item = buildItem();
        item.setOwner(user1);

        item.setRequest(itemRequest);

        ItemDto itemDto = buildItemDto();
        itemDto.setRequestId(1L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto foundItem = itemService.save(user1.getId(), itemDto);

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(item.getId(), foundItem.getId());
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void saveTestWithWrongRequestId() {
        User user = buildUser();

        ItemDto itemDto = buildItemDto();
        itemDto.setRequestId(1L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.save(1L, itemDto));

        Assertions.assertEquals("ItemRequest не найден", exception.getMessage());
    }

    @Test
    void updateTest() {
        User user = new User(1L, "testName", "test@mail.com");

        Item item = buildItem();
        item.setId(1L);
        item.setOwner(user);

        Item itemUpdate = item;
        itemUpdate.setName("testNameUpdate");

        ItemDto itemDto = buildItemDto();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(itemUpdate);

        ItemDto foundItem = itemService.update(user.getId(), item.getId(), itemDto);

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(item.getId(), foundItem.getId());
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void updateTestException() {
        User user = new User(1L, "test", "test@mail.com");
        User user1 = new User(2L, "test1", "test1@mail.com");

        Item item = buildItem();
        item.setId(1L);
        item.setOwner(user);

        Item itemUpdate = item;
        itemUpdate.setName("testNameUpdate");

        ItemDto itemDto = buildItemDto();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(itemUpdate);

        Exception exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(user1.getId(), item.getId(), itemDto));

        Assertions.assertEquals("предмет с идентификатором: 2 не принадлежит пользователю с идентификатором: 1",
                exception.getMessage());
    }

    private ItemDto buildItemDto() {
        return ItemDto.builder()
                .name("test")
                .description("testDescription")
                .available(true)
                .build();
    }

    private Item buildItem() {
        return Item.builder()
                .id(1L)
                .name("test")
                .description("testDescription")
                .available(true)
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .name("test")
                .email("test@mail.com")
                .build();
    }

}
