package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, PageRequest pageRequest) {
        List<User> users;
        if (ids.isEmpty()) {
            users = userRepository.findAll(pageRequest).stream().collect(Collectors.toList());
        } else {
            users = userRepository.findAllByIdIn(ids, pageRequest);
        }
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto add(NewUserRequest dto) {
        User user = userMapper.toUser(dto);
        User newUser = userRepository.save(user);
        return userMapper.toDto(newUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        userRepository.deleteById(userId);
    }
}
