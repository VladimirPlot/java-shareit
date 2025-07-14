package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = new CommentDto(10L, "Текст", "Автор", LocalDateTime.of(2025, 7, 12, 10, 30));
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"Текст\"");
        assertThat(json).contains("\"created\":\"2025-07-12T10:30:00\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{"
                + "\"id\": 10,"
                + "\"text\": \"Текст\","
                + "\"authorName\": \"Автор\","
                + "\"created\": \"2025-07-12T10:30:00\""
                + "}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getText()).isEqualTo("Текст");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 7, 12, 10, 30));
    }
}