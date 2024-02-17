package org.example.monitoringservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * The LoginRequestDto class represents the data transfer object for handling login requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;
    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;
}
