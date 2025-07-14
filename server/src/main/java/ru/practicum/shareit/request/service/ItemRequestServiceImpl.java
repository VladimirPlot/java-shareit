package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository reqRepo;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        ItemRequest req = ItemRequestMapper.toModel(dto, userId);
        req.setRequestor(user);
        req.setCreated(LocalDateTime.now());
        ItemRequest saved = reqRepo.save(req);
        return ItemRequestMapper.toDto(saved, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return reqRepo.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(r -> {
                    var items = itemRepo.findAllByRequestId(r.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toDto(r, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        var page = PageRequest.of(from / size, size);
        return reqRepo.findAllByRequestorIdNotOrderByCreatedDesc(userId, page).stream()
                .map(r -> {
                    var items = itemRepo.findAllByRequestId(r.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toDto(r, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        ItemRequest r = reqRepo.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Request not found: " + requestId));
        var items = itemRepo.findAllByRequestId(r.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toDto(r, items);
    }
}