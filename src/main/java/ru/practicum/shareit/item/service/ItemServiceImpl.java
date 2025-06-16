package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;
    private long nextId = 1;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        UserDto userDto = userService.getUserById(ownerId);
        User owner = UserMapper.toUser(userDto);

        Item createdItem = ItemMapper.toItem(itemDto, owner);
        createdItem.setId(nextId++);
        items.put(createdItem.getId(), createdItem);

        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item existingItem = findItemById(itemId);

        if (!Objects.equals(existingItem.getOwner().getId(), ownerId)) {
            throw new OwnerNotFoundException("Only the owner can update the item");
        }


        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        return ItemMapper.toItemDto(findItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return List.of();

        String lowerText = text.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                        (item.getName().toLowerCase().contains(lowerText) ||
                                item.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item findItemById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Item not found with id: " + itemId);
        }
        return item;
    }
}