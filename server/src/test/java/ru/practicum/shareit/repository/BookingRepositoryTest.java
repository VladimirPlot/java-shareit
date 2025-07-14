package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ItemRepository itemRepo;

    private User owner;
    private User booker;
    private Item item;

    private Booking past;
    private Booking current;
    private Booking future;
    private Booking waiting;
    private Booking rejected;

    @BeforeEach
    void setUp() {
        owner = userRepo.save(new User(null, "Owner", "owner@example.com"));
        booker = userRepo.save(new User(null, "Booker", "booker@example.com"));
        item = itemRepo.save(new Item(null, "Вещь", "desc", true, owner, null));

        LocalDateTime now = LocalDateTime.now();

        past = bookingRepo.save(new Booking(null, now.minusDays(5), now.minusDays(2), item, booker, StatusBooking.APPROVED));
        current = bookingRepo.save(new Booking(null, now.minusHours(1), now.plusHours(2), item, booker, StatusBooking.APPROVED));
        future = bookingRepo.save(new Booking(null, now.plusDays(1), now.plusDays(3), item, booker, StatusBooking.APPROVED));
        waiting = bookingRepo.save(new Booking(null, now.plusDays(2), now.plusDays(4), item, booker, StatusBooking.WAITING));
        rejected = bookingRepo.save(new Booking(null, now.plusDays(3), now.plusDays(5), item, booker, StatusBooking.REJECTED));
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> result = bookingRepo.findAllByBookerIdOrderByStartDesc(booker.getId());
        assertThat(result).containsExactly(rejected, waiting, future, current, past);
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> waitingList = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), StatusBooking.WAITING);
        assertThat(waitingList).containsExactly(waiting);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> currentList = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), now, now);
        assertThat(currentList).containsExactly(current);
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> pastList = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), now);
        assertThat(pastList).containsExactly(past);
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> futureList = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), now);
        assertThat(futureList).containsExactly(rejected, waiting, future);
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> ownerBookings = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(owner.getId());
        assertThat(ownerBookings).containsExactly(rejected, waiting, future, current, past);
    }

    @Test
    void findFirst1ByItemIdAndEndBeforeOrderByEndDesc() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepo.findFirst1ByItemIdAndEndBeforeOrderByEndDesc(item.getId(), now);
        assertThat(result).containsExactly(past);
    }

    @Test
    void findFirst1ByItemIdAndStartAfterOrderByStartAsc() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepo.findFirst1ByItemIdAndStartAfterOrderByStartAsc(item.getId(), now);
        assertThat(result.get(0).getStart()).isAfter(now);
    }
}