package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmailUnique(userDto.getEmail(), null);

        User user = UserMapper.toUser(userDto);
        user.setId(nextId++);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User existingUser = findUserById(userDto.getId());

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmailUnique(userDto.getEmail(), existingUser.getId());
            existingUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(findUserById(userId));
    }

    @Override
    public UserDto removeUserById(Long userId) {
        User user = findUserById(userId);
        users.remove(user.getId());
        return UserMapper.toUserDto(user);
    }

    private User findUserById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return user;
    }

    private void validateEmailUnique(String email, Long excludeId) {
        users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .filter(user -> excludeId == null || !Objects.equals(user.getId(), excludeId))
                .findAny()
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("Email already in use");
                });
    }
}