package ru.practicum.shareit.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
@Getter
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserRepository getUserRepository () {return userRepository;}

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toListOfUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(long id) {
        User user = userRepository.findById(id);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        throwIfEmailDuplicate(userDto);

        User user = userRepository.save(userMapper.toUser(userDto));

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.findById(id);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank() && !user.getEmail().equals(userDto.getEmail())) {
            throwIfEmailDuplicate(userDto);
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        getById(id);
        userRepository.delete(id);
    }

    private void throwIfEmailDuplicate(UserDto userDto) {
        userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .filter(email -> email.equals(userDto.getEmail()))
                .findFirst().ifPresent(email -> {
            throw new BadRequestException(format("пользователь с электронной почтой: %s уже существует", email));
        });
    }
}