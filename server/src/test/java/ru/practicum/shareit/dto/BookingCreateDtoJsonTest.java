package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    private JacksonTester<BookingCreateDto> json;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка Java 8 дат
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testSerialize() throws Exception {
        BookingCreateDto dto = new BookingCreateDto(1L,
                LocalDateTime.of(2025, 7, 13, 12, 0),
                LocalDateTime.of(2025, 7, 14, 12, 0));

        String content = json.write(dto).getJson();

        assertThat(content).contains("\"itemId\":1");
        assertThat(content).contains("\"start\":\"2025-07-13T12:00:00\"");
        assertThat(content).contains("\"end\":\"2025-07-14T12:00:00\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonStr = "{"
                + "\"itemId\": 2,"
                + "\"start\": \"2025-07-20T10:30:00\","
                + "\"end\": \"2025-07-21T10:30:00\""
                + "}";

        BookingCreateDto dto = json.parseObject(jsonStr);

        assertThat(dto.getItemId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 7, 20, 10, 30));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 7, 21, 10, 30));
    }
}