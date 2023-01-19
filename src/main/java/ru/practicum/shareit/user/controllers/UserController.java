package ru.practicum.shareit.user.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @JsonView(Details.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> save(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Запрос создания пользователя");
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(@PathVariable("id") @Min(0) long id,
                                          @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Запрос обновления пользователя c id: {}", id);
        return new ResponseEntity<>(userService.update(id, userDto), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") @Min(0) long id) {
        log.info("Запрос на получение пользователя c id: {}", id);
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("Запрос на получение всех пользователей");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @JsonView(Details.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя c id: {}", id);
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}