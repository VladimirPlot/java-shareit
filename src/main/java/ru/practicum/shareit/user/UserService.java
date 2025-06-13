package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(UserDto userDto);

    public List<UserDto> getAllUsers();

    public UserDto getUserById(long idUser);

    public UserDto removeUserById(long idUser);
}