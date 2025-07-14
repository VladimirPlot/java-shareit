package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toModel_shouldMapCorrectly() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("нужно что-то");
        ItemRequest request = ItemRequestMapper.toModel(dto, 1L);

        assertThat(request.getDescription()).isEqualTo("нужно что-то");
    }

    @Test
    void toDto_shouldMapCorrectly() {
        ItemRequest req = new ItemRequest();
        req.setId(5L);
        req.setDescription("desc");
        req.setCreated(LocalDateTime.now());
        req.setRequestor(new User(2L, "req", "req@x"));

        var result = ItemRequestMapper.toDto(req, List.of(new ItemDto()));
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void toDto_withNullItems_shouldReturnEmptyList() {
        ItemRequest req = new ItemRequest();
        req.setId(1L);
        req.setDescription("desc");
        req.setRequestor(new User(1L, "u", "e"));
        req.setCreated(LocalDateTime.now());

        var dto = ItemRequestMapper.toDto(req, null);
        assertThat(dto.getItems()).isEmpty();
    }
}