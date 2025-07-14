package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.errors.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserNotFound_shouldReturn404() {
        UserNotFoundException ex = new UserNotFoundException("not found");
        ResponseEntity<ErrorResponse> response = handler.handleUserNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("not found");
    }

    @Test
    void handleEmailAlreadyExists_shouldReturn409() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("exists");
        ResponseEntity<ErrorResponse> response = handler.handleEmailAlreadyExists(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("exists");
    }

    @Test
    void handleOwnerNotFound_shouldReturn403() {
        OwnerNotFoundException ex = new OwnerNotFoundException("forbidden");
        ResponseEntity<ErrorResponse> response = handler.handleOwnerNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("forbidden");
    }

    @Test
    void handleItemRequestNotFound_shouldReturn404() {
        ItemRequestNotFoundException ex = new ItemRequestNotFoundException("request");
        ResponseEntity<ErrorResponse> response = handler.handleItemRequestNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("request");
    }

    @Test
    void handleItemNotFound_shouldReturn404() {
        ItemNotFoundException ex = new ItemNotFoundException("request");
        ResponseEntity<ErrorResponse> response = handler.handleItemNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("request");
    }

    @Test
    void handleBookingNotFound_shouldReturn404() {
        BookingNotFoundException ex = new BookingNotFoundException("request");
        ResponseEntity<ErrorResponse> response = handler.handleBookingNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("request");
    }

    @Test
    void handleBookingAlreadyApproved_shouldReturn404() {
        BookingAlreadyApprovedException ex = new BookingAlreadyApprovedException("request");
        ResponseEntity<ErrorResponse> response = handler.handleBookingAlreadyApproved(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("request");
    }

    @Test
    void handleBadRequest_shouldReturn400() {
        BadRequestException ex = new BadRequestException("request");
        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("request");
    }
}