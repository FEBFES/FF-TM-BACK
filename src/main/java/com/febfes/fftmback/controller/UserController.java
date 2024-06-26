package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiEdit;
import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ApiGetOne;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.domain.common.specification.UserSpec;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.mapper.UserMapper;
import com.febfes.fftmback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Get user by its id")
    @ApiGetOne(path = "{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public UserDto getUser(@PathVariable Long id) {
        return userMapper.userViewToUserDto(userService.getUserViewById(id));
    }

    @Operation(summary = "Edit user by its id")
    @ApiEdit(path = "{id}")
    public void editUser(
            @PathVariable Long id,
            @RequestBody EditUserDto editUserDto
    ) {
        userService.updateUser(userMapper.editUserDtoToUser(editUserDto), id);
    }

    @Operation(summary = "Get users with filter")
    @ApiGet()
    public List<UserDto> getUsersWithFilter(UserSpec userSpec) {
        return userService.getUsersByFilter(userSpec).stream()
                .map(userMapper::userViewToUserDto)
                .toList();
    }
}
