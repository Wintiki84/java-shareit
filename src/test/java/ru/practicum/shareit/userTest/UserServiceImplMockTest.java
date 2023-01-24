package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UserServiceImplMockTest {

    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void findAllTest() {
        List<User> users = new ArrayList<>();
        users.add(buildUser());
        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        List<UserDto> usersDto = userService.getAllUsers();

        Assertions.assertNotNull(usersDto);
        Assertions.assertEquals(1, usersDto.size());
    }

    @Test
    void findByIdTest() {
        User user = buildUser();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        UserDto foundUser = userService.getById(user.getId());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getId(), foundUser.getId());
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findByIdTestWithWrongUserId() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.getById(1L));

        Assertions.assertEquals("Пользователь ID 1 не найден", exception.getMessage());
    }

    @Test
    void saveTest() {
        User user = buildUser();
        UserDto userDto = buildUserDto();

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDto foundUser = userService.save(userDto);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getId(), foundUser.getId());
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void updateTest() {
        User user = buildUser();
        User updateUser = User.builder()
                .id(1L)
                .name("testUpdate")
                .email("testUpdate@mail.com")
                .build();
        UserDto userDto = UserDto.builder()
                .name("testUpdate")
                .email("testUpdate@mail.com")
                .build();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(updateUser);

        UserDto foundUser = userService.update(user.getId(), userDto);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(updateUser.getId(), foundUser.getId());
        Assertions.assertEquals(updateUser.getName(), foundUser.getName());
        Assertions.assertEquals(updateUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void updateTestWithWrongUserId() {
        UserDto userDto = buildUserDto();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.update(1L, userDto));

        Assertions.assertEquals("Пользователь ID 1 не найден", exception.getMessage());
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .name("test")
                .email("test@mail.com")
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
