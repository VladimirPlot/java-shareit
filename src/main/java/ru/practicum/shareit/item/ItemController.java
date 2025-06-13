package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.updateItem(itemDto, itemId, ownerId);
    }
}