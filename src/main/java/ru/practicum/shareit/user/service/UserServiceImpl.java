package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        validateEmailUnique(userDto.getEmail(), null);

        User user = UserMapper.toUser(userDto);
        User saved = userRepository.save(user);
        return UserMapper.toUserDto(saved);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User existing = userRepository.findById(userDto.getId()).
                orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + userDto.getId()));

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmailUnique(userDto.getEmail(), existing.getId());
            existing.setEmail(userDto.getEmail());
        }

        User updated = userRepository.save(existing);
        return UserMapper.toUserDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + userId));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto removeUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + userId));
        userRepository.delete(user);
        return UserMapper.toUserDto(user);
    }

    private void validateEmailUnique(String email, Long excludeId) {
        userRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .filter(u -> !Objects.equals(u.getId(), excludeId))
                .findAny()
                .ifPresent(u -> {
                    throw new EmailAlreadyExistsException(
                            "Email already in use");
                });
    }
}