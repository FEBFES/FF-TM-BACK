package com.febfes.fftmback.service.project.patch;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Setter
public abstract class ProjectPatchFieldProcessor {

    protected ProjectPatchFieldProcessor nextProcessor;

    /**
     * Change Project field
     *
     * @param project Project entity to change
     * @param ownerId Owner ID
     * @param patchDto DTO with changes to make
     * @return boolean that shows if field was changed or not
     */
    public abstract boolean patchField(ProjectEntity project, Long ownerId, PatchDto patchDto);

    public void callNextProcessor(ProjectEntity project, Long ownerId, PatchDto patchDto) {
        if (nonNull(nextProcessor)) {
            nextProcessor.patchField(project, ownerId, patchDto);
        }
    }
}
