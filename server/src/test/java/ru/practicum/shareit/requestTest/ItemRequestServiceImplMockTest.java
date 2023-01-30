package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ItemRequestServiceImplMockTest {

    ItemRequestService itemRequestService;
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);
    }

    @Test
    void findAllByUserIdTest() {
        List<ItemRequest> requests = List.of(buildItemRequest(null));

        User user = buildUser();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(Mockito.anyLong()))
                .thenReturn(requests);

        Mockito.when(itemRepository.findAllByRequestIn(requests))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> requestsDto = itemRequestService.findAllByUserId(1L);

        Assertions.assertNotNull(requestsDto);
        Assertions.assertEquals(1, requestsDto.size());
    }

    @Test
    void findAllByUserIdTestWithWrongUserId() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findAllByUserId(1L)
        );
        Assertions.assertEquals("Пользователь ID 1 не найден", exception.getMessage());
    }

    @Test
    void findByIdTest() {
        User user = buildUser();
        ItemRequest request = buildItemRequest(user);
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        Mockito.when(itemRepository.findAllByRequestId(request.getId()))
                .thenReturn(Collections.emptyList());

        ItemRequestDto requestDto = itemRequestService.findById(user.getId(), request.getId());
        Assertions.assertNotNull(requestDto);
    }

    @Test
    void findByIdTestWithWrongUserId() {
        User user = buildUser();
        ItemRequest request = buildItemRequest(user);

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findById(user.getId(), request.getId()));

        Assertions.assertEquals("Пользователь ID 1 не найден", exception.getMessage());
    }

    @Test
    void findByIdTestWithWrongRequestId() {
        User user = buildUser();
        ItemRequest request = buildItemRequest(user);

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findById(user.getId(), request.getId()));

        Assertions.assertEquals("Запрошенный запрос 1 не существует", exception.getMessage());
    }

    @Test
    void saveTest() {
        ItemRequestDto itemRequestDto = buildItemRequestDto();

        User user = buildUser();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);

        ItemRequestDto request = itemRequestService.save(user.getId(), itemRequestDto);

        Assertions.assertNotNull(request);
        Assertions.assertEquals(itemRequestDto.getDescription(), request.getDescription());
    }

    @Test
    void saveTestWithWrongUserId() {
        ItemRequestDto itemRequestDto = buildItemRequestDto();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.save(1L, itemRequestDto)
        );
        Assertions.assertEquals("Пользователь ID 1 не найден", exception.getMessage());
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .name("test")
                .email("test@mail.com")
                .build();
    }

    private ItemRequestDto buildItemRequestDto() {
        return ItemRequestDto.builder()
                .description("testDescription")
                .build();
    }

    private ItemRequest buildItemRequest(User user) {
        return ItemRequest.builder()
                .id(1L)
                .description("testDescription")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }
}