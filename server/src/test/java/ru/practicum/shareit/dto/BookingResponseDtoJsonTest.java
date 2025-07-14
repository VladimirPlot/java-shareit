package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        BookingResponseDto dto = new BookingResponseDto(
                42L,
                LocalDateTime.of(2025, 7, 13, 12, 0),
                LocalDateTime.of(2025, 7, 14, 12, 0),
                StatusBooking.APPROVED,
                new UserDto(1L, "User", "user@example.com"),
                new ItemDto(2L, "Item", "Desc", true, null, null, null, null)
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"start\":\"2025-07-13T12:00:00\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{"
                + "\"id\": 42,"
                + "\"start\": \"2025-07-13T12:00:00\","
                + "\"end\": \"2025-07-14T12:00:00\","
                + "\"status\": \"APPROVED\","
                + "\"booker\": { \"id\": 1, \"name\": \"User\", \"email\": \"user@example.com\" },"
                + "\"item\": { \"id\": 2, \"name\": \"Item\", \"description\": \"Desc\", \"available\": true }"
                + "}";

        BookingResponseDto dto = objectMapper.readValue(json, BookingResponseDto.class);

        assertThat(dto.getStatus()).isEqualTo(StatusBooking.APPROVED);
        assertThat(dto.getBooker().getEmail()).isEqualTo("user@example.com");
    }
}