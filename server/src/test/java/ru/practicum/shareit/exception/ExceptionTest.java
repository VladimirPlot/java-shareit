package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionTest {

    @Test
    void customExceptions_shouldStoreMessages() {
        assertThat(new BadRequestException("bad").getMessage()).isEqualTo("bad");
        assertThat(new BookingAlreadyApprovedException("approved").getMessage()).isEqualTo("approved");
        assertThat(new OwnerNotFoundException("owner").getMessage()).isEqualTo("owner");
        assertThat(new ItemNotFoundException("item").getMessage()).isEqualTo("item");
        assertThat(new UserNotFoundException("user").getMessage()).isEqualTo("user");
        assertThat(new BookingNotFoundException("booking").getMessage()).isEqualTo("booking");
    }
}