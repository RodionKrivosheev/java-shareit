package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto findById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> throwNotFoundException(id));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Transactional
    @Override
    public UserDto update(int id, PatchUserDto patchUserDto) {
        User user = userRepository.findById(id).orElseThrow(() -> throwNotFoundException(id));
        if (patchUserDto.getName() != null) {
            user.setName(patchUserDto.getName());
        }
        if (patchUserDto.getEmail() != null) {
            checkEmail(patchUserDto.getEmail());
            user.setEmail(patchUserDto.getEmail());
        }

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void delete(int id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(userRepository::delete);
    }

    private void checkEmail(String email) {
      if (userRepository.existsByEmail(email)) {
            String message = "Пользователь с электронной почтой " + email + " уже зарегистрирован.";
            log.warn(message);
            throw new UserAlreadyExistException(message);
        }
    }

    private NotFoundException throwNotFoundException(int id) {
        String message = "Пользователь с id " + id + " не найден!.";
        log.warn(message);
        return new NotFoundException(message);
    }
}
