package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.BookingShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingShortDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        BookingShortDto dto = new BookingShortDto(
                99L,
                12L,
                LocalDateTime.of(2025, 7, 11, 9, 0),
                LocalDateTime.of(2025, 7, 11, 15, 0)
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"bookerId\":12");
        assertThat(json).contains("\"start\":\"2025-07-11T09:00:00\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = """
                {
                  "id": 99,
                  "bookerId": 12,
                  "start": "2025-07-11T09:00:00",
                  "end": "2025-07-11T15:00:00"
                }
                """;

        BookingShortDto dto = objectMapper.readValue(json, BookingShortDto.class);

        assertThat(dto.getBookerId()).isEqualTo(12L);
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 7, 11, 15, 0));
    }
}