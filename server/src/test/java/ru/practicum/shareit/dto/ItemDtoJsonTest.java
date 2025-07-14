package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(5L)
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .comments(List.of())
                .requestId(100L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Отвёртка\"");
        assertThat(json).contains("\"requestId\":100");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{"
                + "\"id\": 5,"
                + "\"name\": \"Отвёртка\","
                + "\"description\": \"Крестовая\","
                + "\"available\": true,"
                + "\"comments\": [],"
                + "\"requestId\": 100"
                + "}";

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto.getName()).isEqualTo("Отвёртка");
        assertThat(dto.getAvailable()).isTrue();
    }
}