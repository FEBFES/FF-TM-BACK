package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiCreate;
import com.febfes.fftmback.annotation.ApiDelete;
import com.febfes.fftmback.annotation.ApiEdit;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.config.auth.RoleCheckerComponent;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.parameter.ColumnParameters;
import com.febfes.fftmback.mapper.ColumnMapper;
import com.febfes.fftmback.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Column")
public class ColumnController {

    private final ColumnService columnService;
    private final RoleCheckerComponent roleCheckerComponent;
    private final ColumnMapper columnMapper;

    @Operation(summary = "Create new column in a project with given id")
    @ApiCreate(path = "{projectId}/columns")
    public ColumnDto crateNewColumn(
            @PathVariable Long projectId,
            @RequestBody @Valid ColumnDto columnDto
    ) {
        roleCheckerComponent.checkIfHasRole(projectId, RoleName.MEMBER_PLUS);
        TaskColumnEntity newColumn = columnService.createColumn(
                columnMapper.columnDtoToColumn(columnDto, projectId)
        );
        return columnMapper.columnToColumnDto(newColumn);
    }

    @Operation(summary = "Edit column by its columnId")
    @ApiEdit(path = "{projectId}/columns/{columnId}")
    public ColumnDto editColumn(
            @ParameterObject ColumnParameters pathVars,
            @RequestBody ColumnDto columnDto
    ) {
        roleCheckerComponent.checkIfHasRole(pathVars.projectId(), RoleName.MEMBER_PLUS);
        TaskColumnEntity updatedColumn = columnService.editColumn(columnDto, pathVars.columnId());
        return columnMapper.columnToColumnDto(updatedColumn);
    }

    @Operation(summary = "Delete column by its columnId")
    @ApiDelete(path = "{projectId}/columns/{columnId}")
    public void deleteColumn(@ParameterObject ColumnParameters pathVars) {
        roleCheckerComponent.checkIfHasRole(pathVars.projectId(), RoleName.MEMBER_PLUS);
        columnService.deleteColumn(pathVars.columnId());
    }
}
