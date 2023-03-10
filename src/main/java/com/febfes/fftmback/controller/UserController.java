package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiEdit;
import com.febfes.fftmback.annotation.ApiGetOne;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.mapper.UserMapper;
import com.febfes.fftmback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "Upload user pic")
    @PostMapping(
            path = "/{userId}/user-pic",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void saveUserPic(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile userPic
    ) {
        userService.saveUserPic(userId, userPic);
    }

    @Operation(summary = "Get user pic")
    @GetMapping(
            path = "/{userId}/user-pic",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    public byte[] getImageWithMediaType(@PathVariable Long userId) {
        return userService.getUserPic(userId).getPic();
    }
}
