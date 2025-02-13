package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.dto.TaskFileDto;
import com.febfes.fftmback.dto.UserPicDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    TaskFileDto fileToTaskFileDto(FileEntity file);

    UserPicDto fileToUserPicDto(FileEntity file);
}
