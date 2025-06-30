package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long bookerId, BookingCreateDto createDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + bookerId));

        Item item = itemRepository.findById(createDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found: " + createDto.getItemId()));


        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available for booking");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new OwnerNotFoundException("Owner cannot book own item");
        }
        Booking booking = BookingMapper.toBooking(createDto, booker, item);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Booking not found: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new OwnerNotFoundException("Only owner can approve/reject booking");
        }

        booking.setStatus(approved ? StatusBooking.APPROVED : StatusBooking.REJECTED);
        Booking updated = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Booking not found: " + bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (!userId.equals(ownerId) && !userId.equals(bookerId)) {
            throw new OwnerNotFoundException("Access denied");
        }
        return BookingMapper.toResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByBooker(Long bookerId, String state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + bookerId));
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    bookerId, now, now);
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case "WAITING" ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, StatusBooking.WAITING);
            case "REJECTED" ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, StatusBooking.REJECTED);
            default -> throw new BadRequestException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + ownerId));
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case "CURRENT" -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    ownerId, now, now);
            case "PAST" -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case "FUTURE" -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case "WAITING" ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, StatusBooking.WAITING);
            case "REJECTED" ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, StatusBooking.REJECTED);
            default -> throw new BadRequestException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}