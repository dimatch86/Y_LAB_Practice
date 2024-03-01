package org.example.monitoringservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных ответа аутентификации")
public class AuthResponseDto {

    @Schema(description = "Токен с данными пользователя")
    private String token;
    @Schema(description = "Пользовательский email", example = "Dimatch86@mail.ru")
    private String email;
    @Schema(description = "Роли пользователя", example = "[ROLE_USER]")
    private List<String> roles;
}
