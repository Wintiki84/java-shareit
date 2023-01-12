package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static java.lang.String.format;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private int counterId = 0;
    private Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(long id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() ->
                new NotFoundException(format("пользователь с идентификатором: %d еще не существует", id)));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(long id, User user) {
        user.setId(id);

        users.put(id, user);

        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    private long getId() {
        return ++counterId;
    }
}
