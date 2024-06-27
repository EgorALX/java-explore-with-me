package ru.practicum.users.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;

@Component
public class UserMapper {

    public UserShortDto toUserShortDto(User initiator) {
        return new UserShortDto(initiator.getId(), initiator.getName());
    }

    public User toUser(NewUserRequest dto) {
        return new User(0L, dto.getName(), dto.getEmail());
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }
}
