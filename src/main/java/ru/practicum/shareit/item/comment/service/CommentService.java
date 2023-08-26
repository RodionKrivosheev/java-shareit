package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;

public interface CommentService {
    CommentDto saveComment(Long itemId, Long userId, CommentDto comment);

}
