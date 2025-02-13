package com.febfes.fftmback.service.project;

public interface ProjectFavoriteService {

    void addProjectToFavourite(Long projectId, Long userId);

    void removeProjectFromFavourite(Long projectId, Long userId);
}
