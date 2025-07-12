package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByRequestorAndExcludeRequestor() {
        User user1 = userRepository.save(new User(null, "User1", "u1@mail.com"));
        User user2 = userRepository.save(new User(null, "User2", "u2@mail.com"));

        requestRepository.save(new ItemRequest(null, "Request A", user1, LocalDateTime.now()));
        requestRepository.save(new ItemRequest(null, "Request B", user2, LocalDateTime.now()));

        List<ItemRequest> own = requestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());
        List<ItemRequest> other = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user1.getId(), PageRequest.of(0, 10));

        assertThat(own).hasSize(1);
        assertThat(own.get(0).getDescription()).isEqualTo("Request A");

        assertThat(other).hasSize(1);
        assertThat(other.get(0).getDescription()).isEqualTo("Request B");
    }
}