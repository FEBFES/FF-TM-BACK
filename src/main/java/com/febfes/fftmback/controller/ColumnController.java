package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiCreate;
import com.febfes.fftmback.annotation.ApiDelete;
import com.febfes.fftmback.annotation.ApiEdit;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.mapper.ColumnMapper;
import com.febfes.fftmback.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@Tag(name = "Column")
public class ColumnController {

    private final @NonNull ColumnService columnService;

    @Operation(summary = "Create new column in a project with given id")
    @ApiCreate(path = "{projectId}/columns")
    public ColumnDto crateNewColumn(@PathVariable Long projectId, @RequestBody ColumnDto columnDto) {
        return ColumnMapper.INSTANCE.columnToColumnDto(columnService.createColumn(projectId, columnDto));
    }

    @Operation(summary = "Edit column by its columnId")
    @ApiEdit(path = "{projectId}/columns/{columnId}")
    public void editColumn(@PathVariable Long projectId,
                           @PathVariable Long columnId,
                           @RequestBody ColumnDto columnDto
    ) {
        columnService.editColumn(projectId, columnId, columnDto);
    }

    @Operation(summary = "Delete column by its columnId")
    @ApiDelete(path = "{ignoredProjectId}/columns/{columnId}")
    public void deleteColumn(@PathVariable Long ignoredProjectId, @PathVariable Long columnId) {
        columnService.deleteColumn(columnId);
    }
}
