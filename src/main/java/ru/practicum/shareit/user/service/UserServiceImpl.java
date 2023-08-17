package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        log.info("Получен список всех пользователей.");
        return toUsersDto(userRepository.findAll());
    }

    public UserDto saveUser(UserDto userDto) {
        User user = toUser(userDto);
        validateUser(user);
        log.info("Пользователь сохранен.");
        return toUserDto(userRepository.save(user));
    }

    public UserDto updateUser(UserDto userDto, Long id) {
        User userToUpdate = getUser(id);
        User user = toUser(userDto);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        log.info("Данные пользователя обновлены.");
        return toUserDto(userRepository.save(userToUpdate));
    }

    @Override
    public UserDto getUserById(Long id) {
        return toUserDto(getUser(id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("Пользователь удален.");
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный ID пользователя."));
    }


    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@") ||
                user.getName().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
    }
}
