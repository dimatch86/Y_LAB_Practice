package org.example.monitoringservice.mapper.mapstruct;

import org.example.monitoringservice.dto.response.ActionResponseDto;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.util.DateConverter;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionMapperDelegate implements ActionMapper {

    @Override
    public ActionResponseDto actionToActionResponse(Action action) {
        return ActionResponseDto.builder()
                .actionMethod(action.getActionMethod())
                .actionedBy(action.getActionedBy())
                .createAt(DateConverter.formatDate(action.getCreateAt()))
                .build();
    }

    @Override
    public List<ActionResponseDto> actionListToActionResponseList(List<Action> actions) {
        if (actions == null) {
            return new ArrayList<>();
        }
        return actions.stream()
                .map(this::actionToActionResponse)
                .toList();
    }
}
