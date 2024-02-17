package org.example.monitoringservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for entering user details
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный email")
    private String email;
    @NotBlank(message = "Пароль не должен быть пустым")
    @NotNull
    private String password;
    @NotBlank(message = "Роль должна быть указана")
    @Pattern(regexp = "USER|ADMIN|user|admin")
    private String role;
}
