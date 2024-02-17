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
    @Schema(description = "Лицевой счет пользователя")
    private String personalAccount;
    @Schema(description = "Дата регистрации пользователя")
    private String registrationDate;
}
