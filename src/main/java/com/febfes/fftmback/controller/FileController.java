package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiDelete;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.TaskFileDto;
import com.febfes.fftmback.dto.UserPicDto;
import com.febfes.fftmback.mapper.FileMapper;
import com.febfes.fftmback.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.febfes.fftmback.util.FileUtils.USER_PIC_URN;

@RestController
@RequestMapping("v1/files")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "File")
@Slf4j
public class FileController {

    private final @NonNull FileService fileService;

    @Operation(summary = "Upload user pic")
    @PostMapping(
            path = "user-pic/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserPicDto saveUserPic(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile userPic
    ) throws IOException {
        FileEntity file = fileService.saveFile(userId, userId, EntityType.USER_PIC, userPic);
        return FileMapper.INSTANCE.fileToUserPicDto(file);
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
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public List<TaskFileDto> saveTaskFiles(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long taskId,
            @RequestParam("files") MultipartFile[] files
    ) {
        return Arrays.stream(files)
                .map(file -> saveTaskFile(user.getId(), taskId, file))
                .flatMap(Optional::stream)
                .map(FileMapper.INSTANCE::fileToTaskFileDto)
                .toList();
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

    @Operation(summary = "Delete user pic by userId")
    @ApiDelete(path = "user-pic/{userId}")
    public void deleteUserPic(@PathVariable Long userId) {
        fileService.deleteFileById(fileService.getFile(String.format(USER_PIC_URN, userId)).getId());
    }

    private Optional<FileEntity> saveTaskFile(
            Long userId,
            Long entityId,
            MultipartFile file
    ) {
        try {
            FileEntity fileEntity = fileService.saveFile(userId, entityId, EntityType.TASK, file);
            return Optional.of(fileEntity);
        } catch (IOException e) {
            log.warn(String.format("Task file wasn't saved: %s.", file.getOriginalFilename()), e);
            return Optional.empty();
        }
    }
}
