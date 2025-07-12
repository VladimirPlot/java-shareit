package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ItemRepository itemRepo;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        owner = userRepo.save(new User(null, "owner", "o@mail.com"));
        booker = userRepo.save(new User(null, "booker", "b@mail.com"));
        item = itemRepo.save(new Item(null, "вещь", "описание", true, owner, null));
    }

    @Test
    void createBooking_shouldSucceed() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        BookingResponseDto created = bookingService.createBooking(booker.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void approveBooking_shouldSetApproved() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        var created = bookingService.createBooking(booker.getId(), dto);
        var approved = bookingService.approveBooking(owner.getId(), created.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(StatusBooking.APPROVED);
    }

    @Test
    void getBookingById_shouldWorkForOwnerAndBooker() {
        var dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        var booking = bookingService.createBooking(booker.getId(), dto);

        var byOwner = bookingService.getBookingById(owner.getId(), booking.getId());
        var byBooker = bookingService.getBookingById(booker.getId(), booking.getId());

        assertThat(byOwner.getId()).isEqualTo(booking.getId());
        assertThat(byBooker.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingsByBooker_shouldReturnList() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), dto);

        List<BookingResponseDto> list = bookingService.getBookingsByBooker(booker.getId(), "ALL");

        assertThat(list).hasSize(1);
    }

    @Test
    void getBookingsByOwner_shouldReturnList() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), dto);

        List<BookingResponseDto> list = bookingService.getBookingsByOwner(owner.getId(), "ALL");

        assertThat(list).hasSize(1);
    }
}