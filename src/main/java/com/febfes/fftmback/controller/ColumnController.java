package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiCreate;
import com.febfes.fftmback.annotation.ApiDelete;
import com.febfes.fftmback.annotation.ApiEdit;
import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ColumnController {

    private final @NonNull ColumnService columnService;

    @Operation(summary = "Get all columns")
    @ApiGet(path = "{projectId}/getColumns")
    public List<ColumnDto> getColumns(@PathVariable Long projectId) {
        // TODO delete this endpoint
        return columnService.getColumns(projectId).stream().map(ColumnService::mapToColumnDto).toList();
    }

    @Operation(summary = "Create new column in a project with given id")
    @ApiCreate(path = "{projectId}/addColumn")
    public ColumnDto crateNewColumn(@PathVariable Long projectId, @RequestBody ColumnDto columnDto) {
        return ColumnService.mapToColumnDto(columnService.createColumn(projectId, columnDto));
    }

    @Operation(summary = "Edit column by its columnId")
    @ApiEdit(path = "editColumn/{columnId}")
    public boolean editColumn(@PathVariable Long columnId,
                              @RequestBody ColumnDto columnDto
    ) {
        // TODO: сделать этот метод также, как и в TaskController
        return columnService.editColumn(columnId, columnDto);
    }

    @Operation(summary = "Delete column by its columnId")
    @ApiDelete(path = "deleteColumn/{columnId}")
    public boolean deleteColumn(@PathVariable Long columnId
    ) {
        // TODO: сделать этот метод также, как и в TaskController (можно оставить возврат boolean, но по идее статуса ошибки будет достаточно)
        return columnService.deleteColumn(columnId);
    }


}
