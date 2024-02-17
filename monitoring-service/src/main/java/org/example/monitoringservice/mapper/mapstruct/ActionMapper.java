package org.example.monitoringservice.mapper.mapstruct;

import org.example.monitoringservice.dto.response.ActionResponseDto;
import org.example.monitoringservice.model.audit.Action;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(ActionMapperDelegate.class)
public interface ActionMapper {

    ActionResponseDto actionToActionResponse(Action action);
    List<ActionResponseDto> actionListToActionResponseList(List<Action> actions);
}
