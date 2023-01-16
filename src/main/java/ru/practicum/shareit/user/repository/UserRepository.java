package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User findById(long id);

    List<User> findAll();

    User update(long id, User user);

    void delete(long id);
}