package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void shouldFindAllByRequestId() {
        User owner = userRepository.save(new User(null, "Bob", "bob@example.com"));

        ItemRequest request = new ItemRequest();
        request.setDescription("Need ladder");
        request.setRequestor(owner);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        Item item = new Item(null, "Ladder", "Wooden", true, owner, request);
        itemRepository.save(item);

        List<Item> found = itemRepository.findAllByRequestId(request.getId());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Ladder");
    }
}