package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        User user = userRepository.save(new User(null, "Alice", "alice@example.com"));

        User found = userRepository.findById(user.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getEmail()).isEqualTo("alice@example.com");
    }
}