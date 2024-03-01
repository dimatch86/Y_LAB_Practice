package org.example.monitoringservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;


/**
 * The LoginRequestDto class represents the data transfer object for handling login requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Schema(description = "Модель данных для запроса авторизации")
public class LoginRequestDto {

    @NotNull
    @NotBlank(message = "Email должен присутствовать в запросе")
    @Schema(description = "Пользовательский email", example = "Dimatch86@mail.ru")
    private String email;
    @NotNull
    @NotBlank(message = "Пароль должен присутствовать в запросе")
    @Schema(description = "Пользовательский пароль")
    private String password;
}
