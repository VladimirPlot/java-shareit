package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBooking_shouldMapCorrectly() {
        // given
        User user = new User(1L, "User", "u@mail.com");
        Item item = new Item(2L, "Item", "Desc", true, user, null);
        BookingCreateDto dto = new BookingCreateDto(2L,
                LocalDateTime.of(2025, 8, 1, 12, 0),
                LocalDateTime.of(2025, 8, 2, 12, 0));

        // when
        Booking booking = BookingMapper.toBooking(dto, user, item);

        // then
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(user);
        assertThat(booking.getStart()).isEqualTo(dto.getStart());
        assertThat(booking.getEnd()).isEqualTo(dto.getEnd());
        assertThat(booking.getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void toResponseDto_shouldMapCorrectly() {
        // given
        User user = new User(1L, "User", "u@mail.com");
        Item item = new Item(2L, "Item", "Desc", true, user, null);
        Booking booking = new Booking(
                10L,
                LocalDateTime.of(2025, 8, 10, 10, 0),
                LocalDateTime.of(2025, 8, 11, 10, 0),
                item,
                user,
                StatusBooking.REJECTED
        );

        // when
        BookingResponseDto dto = BookingMapper.toResponseDto(booking);

        // then
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getStatus()).isEqualTo(StatusBooking.REJECTED);
        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getId()).isEqualTo(2L);
    }
}