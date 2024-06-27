package ru.practicum.users.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest);

    UserDto addUser(NewUserRequest dto);

    void deleteUser(Long userId);
}
