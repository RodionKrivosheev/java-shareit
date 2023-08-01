package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;


    private int generateId() {
        return id++;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User saveUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        //String oldEmail = getUserById(user.getId()).getEmail();
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь не найден.");
        }

        users.put(user.getId(), user);
        return user;
    }


    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void deleteUser(int id) {
        users.remove(id);
    }
}
