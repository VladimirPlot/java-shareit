package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserRepository userRepo;

    private User user;

    @BeforeEach
    void setup() {
        user = userRepo.save(new User(null, "req", "req@mail.com"));
    }

    @Test
    void createRequest_shouldReturnDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужен пылесос");
        ItemRequestDto created = requestService.createRequest(user.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужен пылесос");
    }

    @Test
    void getOwnRequests_shouldReturnCreated() {
        requestService.createRequest(user.getId(), new ItemRequestCreateDto("Нужна гитара"));

        List<ItemRequestDto> list = requestService.getOwnRequests(user.getId());

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDescription()).isEqualTo("Нужна гитара");
    }

    @Test
    void getRequestById_shouldReturnCorrect() {
        var created = requestService.createRequest(user.getId(), new ItemRequestCreateDto("Нужен микрофон"));

        ItemRequestDto result = requestService.getRequestById(user.getId(), created.getId());

        assertThat(result.getDescription()).isEqualTo("Нужен микрофон");
    }

    @Test
    void getAllRequests_shouldReturnEmptyIfNoOthers() {
        requestService.createRequest(user.getId(), new ItemRequestCreateDto("нужен стул"));

        List<ItemRequestDto> list = requestService.getAllRequests(user.getId(), 0, 10);

        assertThat(list).isEmpty();
    }

    @Test
    void createRequest_shouldFailForUnknownUser() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужен чайник");

        assertThrows(
                ru.practicum.shareit.exception.UserNotFoundException.class,
                () -> requestService.createRequest(9999L, dto)
        );
    }

    @Test
    void getOwnRequests_shouldFailForUnknownUser() {
        assertThrows(
                ru.practicum.shareit.exception.UserNotFoundException.class,
                () -> requestService.getOwnRequests(9999L)
        );
    }

    @Test
    void getAllRequests_shouldFailForUnknownUser() {
        assertThrows(
                ru.practicum.shareit.exception.UserNotFoundException.class,
                () -> requestService.getAllRequests(9999L, 0, 10)
        );
    }

    @Test
    void getRequestById_shouldFailForUnknownUser() {
        assertThrows(
                ru.practicum.shareit.exception.UserNotFoundException.class,
                () -> requestService.getRequestById(9999L, 1L)
        );
    }

    @Test
    void getRequestById_shouldFailForUnknownRequest() {
        assertThrows(
                ru.practicum.shareit.exception.ItemRequestNotFoundException.class,
                () -> requestService.getRequestById(user.getId(), 9999L)
        );
    }
}