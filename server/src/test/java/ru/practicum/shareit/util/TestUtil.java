package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class TestUtil {

    public static Booking createBooking(Long itemId, Long userId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setId(itemId);

        booking.setBooker(new User());
        booking.getBooker().setId(userId);

        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(StatusBooking.APPROVED); // или WAITING, если не важно
        return booking;
    }

    public static Booking createPastBooking(Long itemId, Long userId) {
        return createBooking(
                itemId,
                userId,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1)
        );
    }

    public static Comment createComment(Long itemId, User author) {
        Comment comment = new Comment();
        comment.setText("Комментарий");
        comment.setItem(new Item());
        comment.getItem().setId(itemId);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}