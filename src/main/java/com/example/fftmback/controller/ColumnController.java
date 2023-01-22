package com.example.fftmback.controller;

import com.example.fftmback.dto.ColumnDto;
import com.example.fftmback.service.ColumnService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ColumnController {

    private final @NonNull ColumnService columnService;

    @GetMapping(path = "{projectId}/getColumns")
    public List<ColumnDto> getColumns(@PathVariable Long projectId) {
        // TODO delete this endpoint
        return columnService.getColumns(projectId).stream().map(ColumnService::mapToColumnDto).toList();
    }

    @PostMapping(path = "{projectId}/addColumn")
    public ColumnDto crateNewColumn(@PathVariable Long projectId, @RequestBody ColumnDto columnDto) {
        return ColumnService.mapToColumnDto(columnService.createColumn(projectId, columnDto));
    }

    @PutMapping(path = "*/editColumn/{columnId}")
    public boolean editColumn(@PathVariable Long columnId,
                              @RequestBody ColumnDto columnDto
    ) {

        return columnService.editColumn(columnId, columnDto);
    }

    @DeleteMapping(path = "*/deleteColumn/{columnId}")
    public boolean deleteColumn(@PathVariable Long columnId
    ) {
        return columnService.deleteColumn(columnId);
    }


}
