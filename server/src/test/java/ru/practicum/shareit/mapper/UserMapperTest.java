package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapCorrectly() {
        User user = new User(1L, "name", "email");
        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getEmail()).isEqualTo("email");
    }

    @Test
    void toUserDto_withNull_shouldReturnNull() {
        assertThat(UserMapper.toUserDto(null)).isNull();
    }

    @Test
    void toUser_shouldMapCorrectly() {
        UserDto dto = new UserDto(1L, "name", "email");
        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("name");
        assertThat(user.getEmail()).isEqualTo("email");
    }

    @Test
    void toUser_withNull_shouldReturnNull() {
        assertThat(UserMapper.toUser(null)).isNull();
    }
}