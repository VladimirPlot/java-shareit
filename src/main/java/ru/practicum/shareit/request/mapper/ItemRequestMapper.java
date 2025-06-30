package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toModel(ItemRequestCreateDto dto, Long requestorId) {
        ItemRequest req = new ItemRequest();
        req.setDescription(dto.getDescription());
        return req;
    }

    public static ItemRequestDto toDto(ItemRequest req, List<ItemDto> items) {
        return new ItemRequestDto(
                req.getId(),
                req.getDescription(),
                req.getRequestor().getId(),
                req.getCreated(),
                items == null ? Collections.emptyList() : items
        );
    }
}