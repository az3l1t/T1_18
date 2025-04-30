package net.az3l1t.aop.mapper;

import net.az3l1t.aop.dto.TaskCreateDto;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.dto.TaskUpdateDto;
import net.az3l1t.aop.dto.kafka.TaskEventDto;
import net.az3l1t.aop.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "userId", target = "userId")
    TaskResponseDto toResponseDto(Task task);

    @Mapping(target = "id", ignore = true)
    Task toEntity(TaskCreateDto taskDto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TaskUpdateDto taskDto, @MappingTarget Task task);

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "userId", target = "userId")
    TaskEventDto toTaskEventDto(Task task);
}
