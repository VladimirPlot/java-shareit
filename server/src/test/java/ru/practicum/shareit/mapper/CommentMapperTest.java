package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toComment_shouldMapCorrectly() {
        CommentDto dto = new CommentDto(null, "text", null, null);
        User author = new User(1L, "a", "a@x");
        Item item = new Item();
        Comment comment = CommentMapper.toComment(dto, item, author);

        assertThat(comment.getText()).isEqualTo("text");
        assertThat(comment.getItem()).isSameAs(item);
        assertThat(comment.getAuthor()).isSameAs(author);
    }

    @Test
    void toDto_shouldMapCorrectly() {
        User author = new User(1L, "a", "a@x");
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("abc");
        comment.setAuthor(author);
        comment.setCreated(java.time.LocalDateTime.now());

        var dto = CommentMapper.toDto(comment);

        assertThat(dto.getText()).isEqualTo("abc");
        assertThat(dto.getAuthorName()).isEqualTo("a");
    }
}