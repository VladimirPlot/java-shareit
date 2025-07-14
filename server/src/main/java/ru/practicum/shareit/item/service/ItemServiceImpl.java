package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + ownerId));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(
                            "Request not found with id: " + itemDto.getRequestId()));
        }

        Item toSave = ItemMapper.toItem(itemDto, owner, request);
        Item saved = itemRepository.save(toSave);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(
                        "Item not found with id: " + itemId));

        if (!Objects.equals(existing.getOwner().getId(), ownerId)) {
            throw new OwnerNotFoundException("Only the owner can update the item");
        }

        if (itemDto.getName() != null) {
            existing.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existing.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existing.setAvailable(itemDto.getAvailable());
        }

        Item updated = itemRepository.save(existing);
        return ItemMapper.toItemDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(
                        "Item not found with id: " + itemId));

        boolean isOwner = item.getOwner().getId().equals(userId);

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;
        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingRepository
                    .findFirst1ByItemIdAndEndBeforeOrderByEndDesc(itemId, now)
                    .stream().findFirst()
                    .map(b -> new BookingShortDto(
                            b.getId(),
                            b.getBooker().getId(),
                            b.getStart(),
                            b.getEnd()))
                    .orElse(null);

            nextBooking = bookingRepository
                    .findFirst1ByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                    .stream().findFirst()
                    .map(b -> new BookingShortDto(
                            b.getId(),
                            b.getBooker().getId(),
                            b.getStart(),
                            b.getEnd()))
                    .orElse(null);
        }

        List<CommentDto> comments = commentRepository
                .findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .map(item -> {
                    Long id = item.getId();

                    BookingShortDto last = bookingRepository
                            .findFirst1ByItemIdAndEndBeforeOrderByEndDesc(id, now)
                            .stream().findFirst()
                            .map(b -> new BookingShortDto(
                                    b.getId(),
                                    b.getBooker().getId(),
                                    b.getStart(),
                                    b.getEnd()))
                            .orElse(null);

                    BookingShortDto next = bookingRepository
                            .findFirst1ByItemIdAndStartAfterOrderByStartAsc(id, now)
                            .stream().findFirst()
                            .map(b -> new BookingShortDto(
                                    b.getId(),
                                    b.getBooker().getId(),
                                    b.getStart(),
                                    b.getEnd()))
                            .orElse(null);

                    List<CommentDto> comments = commentRepository
                            .findByItemIdOrderByCreatedDesc(id)
                            .stream()
                            .map(CommentMapper::toDto)
                            .collect(Collectors.toList());

                    return ItemMapper.toItemDto(item, last, next, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lower = text.toLowerCase();

        return itemRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                        (item.getName().toLowerCase().contains(lower) ||
                                item.getDescription().toLowerCase().contains(lower)))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found: " + itemId));

        boolean hasPastBooking = bookingRepository
                .findAllByBookerIdAndItemIdOrderByEndDesc(userId, itemId).stream()
                .anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now()));
        if (!hasPastBooking) {
            throw new BadRequestException(
                    "User has not completed a booking for this item");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }
}