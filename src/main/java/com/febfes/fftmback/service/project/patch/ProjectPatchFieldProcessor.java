package com.febfes.fftmback.service.project.patch;

import com.febfes.fftmback.dto.PatchDto;
import lombok.Setter;

@Setter
public abstract class ProjectPatchFieldProcessor {

    protected ProjectPatchFieldProcessor nextProcessor;

    public abstract void patchField(Long projectId, Long ownerId, PatchDto patchDto);
}
