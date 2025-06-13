package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long ownerId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long ownerId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItemsByOwner(long ownerId);

    List<ItemDto> searchItems(String text);
}