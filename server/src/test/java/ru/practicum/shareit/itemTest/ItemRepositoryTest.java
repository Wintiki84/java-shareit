package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
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

        item1 = itemRepository.save(Item.builder().name("test1").description("testDescription1 search").available(true)
                .owner(user1).request(request1).build());
        item2 = itemRepository.save(Item.builder().name("test2").description("testDescription2").available(true)
                .owner(user2).request(request2).build());
        item3 = itemRepository.save(Item.builder().name("test3 search").description("testDescription3").available(true)
                .owner(user2).request(request3).build());
    }


    @Test
    void findAllByRequestIdTest() {
        List<Item> results = itemRepository.findAllByRequestId(request1.getId());

        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
    }

    @Test
    void findAllByRequestInTest() {
        List<Item> results = itemRepository.findAllByRequestIn(List.of(request1));

        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

}
