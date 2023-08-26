package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final CommentService commentService;

    private final CommentDto commentDto = CommentDto
            .builder()
            .id(1L)
            .text("новый комментарий")
            .build();
    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("Petr")
            .email("petr@yandex.ru")
            .build();


    @Test
    void testSaveCommentFailWrongItem() {
        userService.saveUser(userDto);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> commentService.saveComment(20L, 1L, commentDto));

        assertThat(e.getMessage(), equalTo("Неверный ID."));
    }

    @Test
    void testSaveCommentFailWrongUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> commentService.saveComment(1L, 20L, commentDto));

        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

}
