package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return toItemDto(item, null, null, Collections.emptyList());
    }

    public static ItemDto toItemDto(Item item,
                                    BookingShortDto lastBooking,
                                    BookingShortDto nextBooking,
                                    List<CommentDto> comments) {
        if (item == null) {
            return null;
        }
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        if (itemDto == null) return null;
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner, null);
    }
}
