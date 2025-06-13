package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUserById(@PathVariable long id) {
        return userService.removeUserById(id);
    }
}