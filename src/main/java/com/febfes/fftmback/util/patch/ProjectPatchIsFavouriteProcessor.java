package com.febfes.fftmback.util.patch;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.service.ProjectService;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

@Slf4j
public class ProjectPatchIsFavouriteProcessor extends ProjectPatchFieldProcessor {

    private static final String FIELD_NAME = "isFavourite";

    public ProjectPatchIsFavouriteProcessor(
            ProjectService projectService,
            ProjectPatchFieldProcessor nextProcessor
    ) {
        super(projectService, nextProcessor);
    }

    @Override
    public void patchField(
            Long projectId,
            Long ownerId,
            PatchDto patchDto
    ) {
        if (!FIELD_NAME.equals(patchDto.key())) {
            callNextProcessor(projectId, ownerId, patchDto);
            return;
        }

        PatchOperation operation = PatchOperation.getByCode(patchDto.op());
        if (PatchOperation.UPDATE.equals(operation)) {
            Boolean isFavourite = (Boolean) patchDto.value();
            if (Boolean.TRUE.equals(isFavourite)) {
                projectService.addProjectToFavourite(projectId, ownerId);
            } else {
                projectService.removeProjectFromFavourite(projectId, ownerId);
            }
            log.info("Project field \"{}\" updated. New value: {}", FIELD_NAME, isFavourite);
        }
    }

    private void callNextProcessor(
            Long projectId,
            Long ownerId,
            PatchDto patchDto
    ) {
        if (nonNull(nextProcessor)) {
            nextProcessor.patchField(projectId, ownerId, patchDto);
        }
    }
}
