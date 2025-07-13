package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemDto_shouldMapCorrectly() {
        Item item = new Item(1L, "item", "desc", true, new User(2L, "owner", "o@ex.com"), null);
        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("item");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void toItem_shouldMapCorrectly() {
        ItemDto dto = new ItemDto(1L, "item", "desc", true, null, null, List.of(), null);
        User owner = new User(2L, "owner", "o@ex.com");
        ItemRequest request = new ItemRequest();
        request.setId(3L);

        Item item = ItemMapper.toItem(dto, owner, request);

        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    void toItemDto_withNull_shouldReturnNull() {
        assertThat(ItemMapper.toItemDto(null)).isNull();
    }

    @Test
    void toItem_withNull_shouldReturnNull() {
        assertThat(ItemMapper.toItem(null, null, null)).isNull();
    }
}