package com.febfes.fftmback.controller;

import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ColumnController {

    private final @NonNull ColumnService columnService;

    @Operation(summary = "Get all columns")
    @GetMapping(path = "{projectId}/getColumns")
    public List<ColumnDto> getColumns(@PathVariable Long projectId) {
        // TODO delete this endpoint
        return columnService.getColumns(projectId).stream().map(ColumnService::mapToColumnDto).toList();
    }

    @Operation(summary = "Create new column in a project with given id")
    @PostMapping(path = "{projectId}/addColumn")
    public ColumnDto crateNewColumn(@PathVariable Long projectId, @RequestBody ColumnDto columnDto) {
        return ColumnService.mapToColumnDto(columnService.createColumn(projectId, columnDto));
    }

    @Operation(summary = "Edit column by its columnId")
    @PutMapping(path = "editColumn/{columnId}")
    public boolean editColumn(@PathVariable Long columnId,
                              @RequestBody ColumnDto columnDto
    ) {

        return columnService.editColumn(columnId, columnDto);
    }

    @Operation(summary = "Delete column by its columnId")
    @DeleteMapping(path = "deleteColumn/{columnId}")
    public boolean deleteColumn(@PathVariable Long columnId
    ) {
        return columnService.deleteColumn(columnId);
    }


}
