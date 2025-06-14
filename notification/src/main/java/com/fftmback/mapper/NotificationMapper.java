package com.fftmback.mapper;

import com.fftmback.domain.NotificationEntity;
import com.fftmback.dto.NotificationDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    List<NotificationDto> notificationsToDto(List<NotificationEntity> notification);
}
