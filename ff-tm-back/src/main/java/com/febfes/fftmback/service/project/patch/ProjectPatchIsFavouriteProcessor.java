package com.febfes.fftmback.service.project.patch;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.service.project.ProjectFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("projectPatchIsFavouriteProcessor")
@RequiredArgsConstructor
public class ProjectPatchIsFavouriteProcessor extends ProjectPatchFieldProcessor {

    private final ProjectFavoriteService projectFavoriteService;

    private static final String FIELD_NAME = "isFavourite";

    @Override
    public void patchField(ProjectEntity project, Long ownerId, PatchDto patchDto) {
        if (!FIELD_NAME.equals(patchDto.key())) {
            callNextProcessor(project, ownerId, patchDto);
            return;
        }

        if (PatchOperation.UPDATE.equals(patchDto.op())) {
            Boolean isFavourite = (Boolean) patchDto.value();
            Long projectId = project.getId();
            if (Boolean.TRUE.equals(isFavourite)) {
                projectFavoriteService.addProjectToFavourite(projectId, ownerId);
            } else {
                projectFavoriteService.removeProjectFromFavourite(projectId, ownerId);
            }
            log.info("Project field \"{}\" updated. New value: {}", FIELD_NAME, isFavourite);
        }
    }
}
