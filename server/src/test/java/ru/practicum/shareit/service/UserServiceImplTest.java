package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = userService.createUser(new UserDto(null, "Alice", "alice@example.com"));
        user2 = userService.createUser(new UserDto(null, "Bob", "bob@example.com"));
    }

    @Test
    void createUser_shouldPersistUser() {
        UserDto newUser = new UserDto(null, "Charlie", "charlie@example.com");

        UserDto created = userService.createUser(newUser);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Charlie");
        assertThat(created.getEmail()).isEqualTo("charlie@example.com");
    }

    @Test
    void createUser_withDuplicateEmail_shouldThrowException() {
        UserDto duplicate = new UserDto(null, "Alice2", "alice@example.com");

        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void updateUser_shouldModifyUser() {
        UserDto update = new UserDto(user1.getId(), "Alice Updated", "alice.updated@example.com");

        UserDto updated = userService.updateUser(update);

        assertThat(updated.getName()).isEqualTo("Alice Updated");
        assertThat(updated.getEmail()).isEqualTo("alice.updated@example.com");
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrowException() {
        UserDto update = new UserDto(user1.getId(), "Alice", "bob@example.com");

        assertThatThrownBy(() -> userService.updateUser(update))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto found = userService.getUserById(user1.getId());

        assertThat(found.getId()).isEqualTo(user1.getId());
        assertThat(found.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void getUserById_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.getUserById(9999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsers_shouldReturnAll() {
        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void removeUser_shouldDelete() {
        UserDto removed = userService.removeUserById(user1.getId());

        assertThat(removed.getId()).isEqualTo(user1.getId());
    }
}