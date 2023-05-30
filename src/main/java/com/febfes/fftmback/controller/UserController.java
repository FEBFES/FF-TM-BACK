package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.mapper.UserMapper;
import com.febfes.fftmback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "User")
public class UserController {

    private final @NonNull UserService userService;

    @Operation(summary = "Get user by its id")
    @ApiGetOne(path = "{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public UserDto getUser(@PathVariable Long id) {
        return UserMapper.INSTANCE.userToUserDto(userService.getUserById(id));
    }

    @Operation(summary = "Edit user by its id")
    @ApiEdit(path = "{id}")
    public void editUser(
            @PathVariable Long id,
            @RequestBody EditUserDto editUserDto
    ) {
        userService.updateUser(UserMapper.INSTANCE.editUserDtoToUser(editUserDto), id);
    }

    @Operation(summary = "Get users with filter")
    @ApiGet()
    public List<UserDto> getUsersWithFilter(@RequestParam(value = "filter") @FilterParam String filter) {
        return userService.getUsersByFilter(filter).stream()
                .map(UserMapper.INSTANCE::userToUserDto)
                .collect(Collectors.toList());
    }
}
