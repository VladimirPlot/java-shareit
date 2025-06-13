package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();
    private long nextId = 1;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto == null) {
            throw new BadRequestException("User data is required");
        }

        validateEmailUnique(userDto.getEmail(), null);

        User user = UserMapper.toUser(userDto);
        user.setId(nextId++);
        users.add(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (userDto == null) {
            throw new BadRequestException("User data is required");
        }

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
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(findUserById(userId));
    }

    @Override
    public UserDto removeUserById(long userId) {
        User user = findUserById(userId);
        users.remove(user);
        return UserMapper.toUserDto(user);
    }

    private User findUserById(long userId) {
        return users.stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    private void validateEmailUnique(String email, Long excludeId) {
        users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .filter(user -> excludeId == null || user.getId() != excludeId)
                .findAny()
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("Email already in use");
                });
    }
}