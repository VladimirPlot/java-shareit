package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    private final UserDto userDto = new UserDto(1L, "User", "user@example.com");
    private final ItemDto itemDto = new ItemDto(1L, "Item", "Desc", true, null, null, List.of(), null);
    private final BookingResponseDto bookingResponse = new BookingResponseDto(
            1L,
            LocalDateTime.of(2025, 7, 20, 12, 0),
            LocalDateTime.of(2025, 7, 21, 12, 0),
            StatusBooking.WAITING,
            userDto,
            itemDto
    );

    @Test
    void createBooking_shouldReturnCreated() throws Exception {
        BookingCreateDto createDto = new BookingCreateDto(1L, bookingResponse.getStart(), bookingResponse.getEnd());

        Mockito.when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()));
    }

    @Test
    void approveBooking_shouldReturnApproved() throws Exception {
        BookingResponseDto approved = new BookingResponseDto(
                bookingResponse.getId(),
                bookingResponse.getStart(),
                bookingResponse.getEnd(),
                StatusBooking.APPROVED,
                userDto,
                itemDto
        );

        Mockito.when(bookingService.approveBooking(1L, 1L, true)).thenReturn(approved);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getBookingsByBooker_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingsByBooker(1L, "ALL")).thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getBookingsByOwner_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingsByOwner(1L, "ALL")).thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void createBooking_userNotFound_shouldReturn404() throws Exception {
        BookingCreateDto createDto = new BookingCreateDto(1L, bookingResponse.getStart(), bookingResponse.getEnd());

        Mockito.when(bookingService.createBooking(anyLong(), any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveBooking_ownerNotFound_shouldReturn403() throws Exception {
        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new OwnerNotFoundException("Access denied"));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 99L)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void approveBooking_alreadyApproved_shouldReturn400() throws Exception {
        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new BadRequestException("Already approved"));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById_notFound_shouldReturn404() throws Exception {
        Mockito.when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException("Booking not found"));

        mvc.perform(get("/bookings/99")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}