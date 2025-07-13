package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Нужна отвертка",
            1L,
            LocalDateTime.of(2025, 7, 12, 12, 0),
            List.of(new ItemDto(1L, "Отвертка", "Крестовая", true, null, null, List.of(), 1L))
    );

    @Test
    void createRequest_shouldReturnRequestDto() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужна отвертка");

        Mockito.when(itemRequestService.createRequest(eq(1L), any())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value("Нужна отвертка"));
    }

    @Test
    void getOwnRequests_shouldReturnList() throws Exception {
        Mockito.when(itemRequestService.getOwnRequests(1L)).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        Mockito.when(itemRequestService.getAllRequests(1L, 0, 10)).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        Mockito.when(itemRequestService.getRequestById(1L, 1L)).thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createRequest_withoutBody_shouldReturn400() throws Exception {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_notFound_shouldReturn404() throws Exception {
        Mockito.when(itemRequestService.getRequestById(1L, 999L))
                .thenThrow(new ItemRequestNotFoundException("Request not found"));

        mvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}