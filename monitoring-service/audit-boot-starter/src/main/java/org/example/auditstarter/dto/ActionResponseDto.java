package org.example.auditstarter.dto;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Модель данных для представления действия пользователя")
public class ActionResponseDto {
    @Schema(description = "Выполненное действие пользователя")

    private String actionMethod;

    /**
     * The user who performed the action. Defaults to the current user's email in UserContext.
     */
    @Schema(description = "Кем выполнено действие")
    private String actionedBy;

    /**
     * The time when the action was created. Defaults to the current time.
     */
    @Schema(description = "Дата выполнения действия")
    private String createAt;

    public String getActionMethod() {
        return actionMethod;
    }

    public String getActionedBy() {
        return actionedBy;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }

    public void setActionedBy(String actionedBy) {
        this.actionedBy = actionedBy;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
