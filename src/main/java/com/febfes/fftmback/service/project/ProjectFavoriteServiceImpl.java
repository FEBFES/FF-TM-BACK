package com.febfes.fftmback.service.project;

import com.febfes.fftmback.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectFavoriteServiceImpl implements ProjectFavoriteService {

    private final ProjectRepository projectRepository;

    @Override
    public void addProjectToFavourite(Long projectId, Long userId) {
        if (projectRepository.isProjectFavourite(projectId, userId)) {
            projectRepository.addProjectToFavourite(projectId, userId);
        }
    }

    @Override
    public void removeProjectFromFavourite(Long projectId, Long userId) {
        projectRepository.removeProjectFromFavourite(projectId, userId);
    }
}
