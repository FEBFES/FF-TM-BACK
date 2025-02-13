package com.febfes.fftmback.service.project.patch;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Setter
public abstract class ProjectPatchFieldProcessor {

    protected ProjectPatchFieldProcessor nextProcessor;

    public abstract void patchField(ProjectEntity project, Long ownerId, PatchDto patchDto);

    public void callNextProcessor(ProjectEntity project, Long ownerId, PatchDto patchDto) {
        if (nonNull(nextProcessor)) {
            nextProcessor.patchField(project, ownerId, patchDto);
        }
    }
}
