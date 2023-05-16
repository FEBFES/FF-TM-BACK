package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiDelete;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("v1/files")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "File")
public class FileController {

    private final @NonNull FileService fileService;

    @Operation(summary = "Upload user pic")
    @PostMapping(
            path = "user-pic/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void saveUserPic(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile userPic
    ) {
        fileService.saveFile(userId, userId, EntityType.USER_PIC, userPic);
    }

    @Operation(summary = "Get user pic")
    @GetMapping(
            path = "user-pic/{userId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    public byte[] getImageWithMediaType(@PathVariable Long userId) throws IOException {
        return fileService.getFileContent(userId.toString(), EntityType.USER_PIC);
    }

    @Operation(summary = "Upload task files")
    @PostMapping(
            path = "task/{taskId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void saveTaskFiles(@PathVariable Long taskId, @RequestParam("files") MultipartFile[] files) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Arrays.stream(files).forEach(file -> fileService.saveFile(user.getId(), taskId, EntityType.TASK, file));
    }

    @Operation(summary = "Get task file")
    @GetMapping(
            path = "task/{fileId}",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    public byte[] getTaskFile(@PathVariable String fileId) throws IOException {
        return fileService.getFileContent(fileId, EntityType.TASK);
    }

    @Operation(summary = "Delete file by its id")
    @ApiDelete(path = "{fileId}")
    public void deleteFile(@PathVariable Long fileId) {
        fileService.deleteFileById(fileId);
    }
}