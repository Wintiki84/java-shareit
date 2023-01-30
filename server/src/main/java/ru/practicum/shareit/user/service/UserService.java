package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserService {

    @NotNull
    List<UserDto> getAllUsers();

    @NotNull
    UserDto getById(@NotNull Long id);

    @NotNull
    UserDto save(@NotNull UserDto userDto);

    @NotNull
    UserDto update(@NotNull Long id, @NotNull UserDto userDto);

    void delete(@NotNull Long id);
}