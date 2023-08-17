package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto findById(int id);

    List<UserDto> findAll();

    UserDto update(int id, PatchUserDto patchUserDto);

    void delete(int id);
}
