package org.example.monitoringservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для представления пользователя")
public class UserResponseDto {

    @Schema(description = "Пользовательский email", example = "Dimatch86@mail.ru")
    private String email;
    @Schema(description = "Лицевой счет пользователя", example = "9e597713-8825-4579-b3df-f26ee1f58497")
    private String personalAccount;
    @Schema(description = "Дата регистрации пользователя", example = "2024-02-23 08:57:50")
    private String registrationDate;
}
