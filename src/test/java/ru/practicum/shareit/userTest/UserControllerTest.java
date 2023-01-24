package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controllers.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private UserController userController;

    private UserDto user;

    @BeforeEach
    void init() {
        user = buildUserDto();
    }

    @Test
    void findByIdTestWithWrongUserId() {
        assertThrows(NotFoundException.class,
                () -> userController.getById(1L));
    }

    @Test
    void saveTest() {
        UserDto userDto = userController.save(user).getBody();
        assertEquals(userDto.getId(), userController.getById(userDto.getId()).getBody().getId());
    }

    @Test
    void updateTest() {
        userController.save(user);
        UserDto userDto = buildUserDto();
        userDto.setName("testUpdate");
        userDto.setEmail("testUpdate@mail.com");

        userController.update(1L, userDto);
        assertEquals(userDto.getEmail(), userController.getById(1L).getBody().getEmail());
    }

    @Test
    void updateTestWithWrongUserId() {
        assertThrows(NotFoundException.class,
                () -> userController.update(1L, user));
    }

    @Test
    void deleteTest() {
        UserDto userDto = userController.save(user).getBody();
        assertEquals(1, userController.getAll().getBody().size());
        userController.delete(userDto.getId());
        assertEquals(0, userController.getAll().getBody().size());
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .name("test")
                .email("test@mail.com")
                .build();
    }

}
