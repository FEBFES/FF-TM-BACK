package com.febfes.fftmback.service.project.patch;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Slf4j
@Service("projectPatchCommonProcessor")
@RequiredArgsConstructor
public class ProjectPatchCommonProcessor extends ProjectPatchFieldProcessor {

    @Override
    public boolean patchField(ProjectEntity project, Long ownerId, PatchDto patchDto) {
        if (PatchOperation.UPDATE.equals(patchDto.op())) {
            updateProjectField(patchDto, project);
            return true;
        } else {
            callNextProcessor(project, ownerId, patchDto);
            return false;
        }
    }

    private void updateProjectField(
            PatchDto patchDto,
            ProjectEntity projectEntity
    ) {
        try {
            Field field = projectEntity.getClass().getDeclaredField(patchDto.key());
            field.setAccessible(true);
            ReflectionUtils.setField(field, projectEntity, patchDto.value());
            log.info("Project field \"{}\" updated. New value: {}", patchDto.key(), patchDto.value());
        } catch (NoSuchFieldException e) {
            log.warn("Can't find field \"{}\" in Project entity: {}", patchDto.key(), e.getMessage());
        }
    }
}
