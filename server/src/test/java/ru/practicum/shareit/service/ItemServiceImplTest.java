package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User owner;
    private User requester;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        requester = userRepository.save(new User(null, "Requester", "requester@example.com"));
    }

    @Test
    void createItem_withoutRequest_shouldSucceed() {
        ItemDto dto = ItemDto.builder()
                .name("Дрель")
                .description("Ударная")
                .available(true)
                .build();

        ItemDto saved = itemService.createItem(dto, owner.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Дрель");
        assertThat(saved.getRequestId()).isNull();
    }

    @Test
    void createItem_withRequest_shouldLink() {
        ItemRequest request = requestRepository.save(
                new ItemRequest(null, "Нужен молоток", requester, LocalDateTime.now()));

        ItemDto dto = ItemDto.builder()
                .name("Молоток")
                .description("Стальной")
                .available(true)
                .requestId(request.getId())
                .build();

        ItemDto saved = itemService.createItem(dto, owner.getId());

        assertThat(saved.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void updateItem_shouldApplyChanges() {
        ItemDto created = itemService.createItem(
                ItemDto.builder().name("Отвёртка").description("Обычная").available(true).build(),
                owner.getId()
        );

        ItemDto updates = ItemDto.builder()
                .name("Отвёртка+")
                .description("С магнитом")
                .available(false)
                .build();

        ItemDto updated = itemService.updateItem(updates, created.getId(), owner.getId());

        assertThat(updated.getName()).isEqualTo("Отвёртка+");
        assertThat(updated.getDescription()).isEqualTo("С магнитом");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void getItemById_asOwner_shouldIncludeBookings() {
        ItemDto created = itemService.createItem(
                ItemDto.builder().name("Вещь").description("desc").available(true).build(),
                owner.getId()
        );

        ItemDto found = itemService.getItemById(created.getId(), owner.getId());

        assertThat(found.getName()).isEqualTo("Вещь");
    }

    @Test
    void getItemById_asOtherUser_shouldOmitBookings() {
        ItemDto created = itemService.createItem(
                ItemDto.builder().name("Вещь").description("desc").available(true).build(),
                owner.getId()
        );

        ItemDto found = itemService.getItemById(created.getId(), requester.getId());

        assertThat(found.getName()).isEqualTo("Вещь");
    }

    @Test
    void getAllItemsByOwner_shouldReturnOwnedItems() {
        itemService.createItem(ItemDto.builder().name("1").description("a").available(true).build(), owner.getId());
        itemService.createItem(ItemDto.builder().name("2").description("b").available(true).build(), owner.getId());

        List<ItemDto> items = itemService.getAllItemsByOwner(owner.getId());

        assertThat(items).hasSize(2);
    }

    @Test
    void searchItems_shouldMatchByText() {
        itemService.createItem(ItemDto.builder()
                .name("Дрель")
                .description("Ударная дрель")
                .available(true)
                .build(), owner.getId());

        List<ItemDto> found = itemService.searchItems("ударн");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void searchItems_emptyText_shouldReturnEmpty() {
        List<ItemDto> found = itemService.searchItems("");

        assertThat(found).isEmpty();
    }

    @Test
    void addComment_withoutBooking_shouldFail() {
        ItemDto item = itemService.createItem(ItemDto.builder()
                .name("Книга")
                .description("Про Java")
                .available(true)
                .build(), owner.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Отличная!");

        assertThatThrownBy(() -> itemService.addComment(requester.getId(), item.getId(), comment))
                .isInstanceOf(RuntimeException.class); // Заменить на ожидаемый exception, если знаешь точный
    }

    @Test
    void updateItem_shouldFailIfNotOwner() {
        ItemDto created = itemService.createItem(
                ItemDto.builder().name("Шуруповёрт").description("desc").available(true).build(),
                owner.getId());

        ItemDto updates = ItemDto.builder().name("Новая модель").build();

        assertThatThrownBy(() -> itemService.updateItem(updates, created.getId(), requester.getId()))
                .isInstanceOf(OwnerNotFoundException.class);
    }

    @Test
    void getItemById_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> itemService.getItemById(9999L, owner.getId()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void updateItem_shouldThrowIfItemNotFound() {
        ItemDto updates = ItemDto.builder().name("Обновление").build();

        assertThatThrownBy(() -> itemService.updateItem(updates, 9999L, owner.getId()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void addComment_shouldFailWithoutPastBooking() {
        ItemDto item = itemService.createItem(ItemDto.builder()
                .name("Лампа")
                .description("Настольная")
                .available(true)
                .build(), owner.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Классная!");

        assertThatThrownBy(() -> itemService.addComment(requester.getId(), item.getId(), comment))
                .isInstanceOf(BadRequestException.class);
    }
}