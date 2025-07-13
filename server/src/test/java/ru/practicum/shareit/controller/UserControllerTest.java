package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto input = new UserDto(null, "John", "john@example.com");
        UserDto saved = new UserDto(1L, "John", "john@example.com");

        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(saved);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "john@example.com"),
                new UserDto(2L, "Jane", "jane@example.com")
        );

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UserDto user = new UserDto(1L, "John", "john@example.com");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto update = new UserDto(1L, "John Updated", "john@new.com");
        Mockito.when(userService.updateUser(any(UserDto.class))).thenReturn(update);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john@new.com")));
    }

    @Test
    void deleteUser_shouldReturnDeletedUser() throws Exception {
        UserDto user = new UserDto(1L, "John", "john@example.com");
        Mockito.when(userService.removeUserById(1L)).thenReturn(user);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void getUserById_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_notFound_shouldReturn404() throws Exception {
        UserDto dto = new UserDto(99L, "Name", "email@example.com");

        Mockito.when(userService.updateUser(any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(patch("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}