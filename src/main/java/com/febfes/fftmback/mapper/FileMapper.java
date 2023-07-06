package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.dto.TaskFileDto;
import com.febfes.fftmback.dto.UserPicDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileMapper {

    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    TaskFileDto fileToTaskFileDto(FileEntity file);

    UserPicDto fileToUserPicDto(FileEntity file);
}
