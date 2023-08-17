package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestDto {
    private int id;
    private String description;
    private UserDto requestor;
    private LocalDateTime create;
}
