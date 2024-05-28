package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.NotificationEntity;
import com.febfes.fftmback.dto.NotificationDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    List<NotificationDto> notificationsToDto(List<NotificationEntity> notification);
}
