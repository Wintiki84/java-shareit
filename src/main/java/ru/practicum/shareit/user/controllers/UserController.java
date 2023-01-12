package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос создания пользователя");
        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @PathVariable long id,
                          @RequestBody UserDto userDto) {
        log.info("Запрос обновления пользователя c id: {}", id);
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Запрос на получение пользователя c id: {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя c id: {}", id);
        userService.delete(id);
    }
}