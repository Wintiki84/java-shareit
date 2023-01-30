package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Positive;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @JsonView(Details.class)
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getUsers();
    }

    @JsonView(Details.class)
    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive Long userId) {
        log.info("Запрос на получение пользователя c id: {}", userId);
        return userClient.getUser(userId);
    }

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(
            @Validated(Create.class)
            @RequestBody UserRequestDto requestDto) {
        log.info("Запрос создания пользователя");
        return userClient.createUser(requestDto);
    }

    @JsonView(Details.class)
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(
            @PathVariable @Positive Long id,
            @Validated(Update.class)
            @RequestBody UserRequestDto requestDto) {
        log.info("Запрос обновления пользователя c id: {}", id);
        return userClient.updateUser(id, requestDto);
    }

    @JsonView(Details.class)
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long userId) {
        log.info("Запрос на удаление пользователя c id: {}", userId);
        return userClient.deleteUser(userId);
    }
}
