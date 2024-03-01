package org.example.monitoringservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * Data model for entering user details
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Schema(description = "Модель данных для запроса регистрации")
public class UserDto {
    @NotNull
    @NotBlank(message = "Email должен присутствовать в запросе")
    @Email(message = "Некорректный ввод email")
    @Schema(description = "Пользовательский email", example = "Dimatch86@mail.ru")
    private String email;
    @NotNull
    @NotBlank(message = "Пароль должен присутствовать в запросе")
    @Schema(description = "Пользовательский пароль")
    private String password;
    @NotNull
    @NotBlank(message = "Роль должна присутствовать в запросе")
    @Pattern(regexp = "USER|ADMIN|user|admin", message = "Некорректный ввод роли")
    @Schema(description = "Роль пользователя", example = "USER")
    private String role;
}
