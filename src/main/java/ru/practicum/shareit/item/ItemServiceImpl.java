package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final List<Item> items = new ArrayList<>();
    private final UserService userService;
    private long nextId = 1;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long ownerId) {
        if (itemDto == null) {
            throw new BadRequestException("Item data is required");
        }

        UserDto userDto = userService.getUserById(ownerId);
        User owner = UserMapper.toUser(userDto);

        Item createdItem = ItemMapper.toItem(itemDto, owner);
        createdItem.setId(nextId++);
        items.add(createdItem);

        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long ownerId) {
        if (itemDto == null) {
            throw new BadRequestException("Item data is required");
        }

        Item existingItem = findItemById(itemId);

        if (existingItem.getOwner().getId() != ownerId) {
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
    public ItemDto getItemById(long itemId, long userId) {
        return ItemMapper.toItemDto(findItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(long ownerId) {
        return items.stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return List.of();

        String lowerText = text.toLowerCase();

        return items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                        (item.getName().toLowerCase().contains(lowerText) ||
                                item.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item findItemById(long itemId) {
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));
    }
}