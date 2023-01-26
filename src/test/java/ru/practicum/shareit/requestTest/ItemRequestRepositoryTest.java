package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    ItemRequest request1;
    ItemRequest request2;
    ItemRequest request3;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(User.builder().name("test1").email("test1@mail.ru").build());
        user2 = userRepository.save(User.builder().name("test2").email("test2@mail.ru").build());

        request1 = itemRequestRepository.save(ItemRequest.builder().description("testDescription1").requestor(user1)
                .created(LocalDateTime.now()).build());
        request2 = itemRequestRepository.save(ItemRequest.builder().description("testDescription2").requestor(user1)
                .created(LocalDateTime.now()).build());
        request3 = itemRequestRepository.save(ItemRequest.builder().description("testDescription3").requestor(user2)
                .created(LocalDateTime.now()).build());

    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdOrderByCreatedAscTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(user1.getId());

        Assertions.assertFalse(requests.isEmpty());
        Assertions.assertEquals(2, requests.size());
    }

    @Test
    void findAllByRequestorIdNotLikeTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotLike(
                user1.getId(),
                Pageable.unpaged());

        Assertions.assertFalse(requests.isEmpty());
        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(request3.getId(), requests.get(0).getId());
    }
}
