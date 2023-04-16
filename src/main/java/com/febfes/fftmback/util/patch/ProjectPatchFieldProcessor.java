package com.febfes.fftmback.util.patch;

import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.service.ProjectService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ProjectPatchFieldProcessor {

    protected ProjectService projectService;

    protected ProjectPatchFieldProcessor nextProcessor;

    abstract public void patchField(Long projectId, Long ownerId, PatchDto patchDto);
}
