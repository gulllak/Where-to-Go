package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.NewUserRequest;
import ru.practicum.mainservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(NewUserRequest newUser);

    void delete(long userId);

    List<UserDto> getUsers(List<Long> ids, int from, int size);
}
