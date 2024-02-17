package org.example.monitoringservice.in.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Контроллер для авторизации пользователя")
public interface SwaggerAuthController {

    @Operation(summary = "Зарегистрировать новый аккаунт")
    @ApiResponse(responseCode = "200", description = "Аккаунт успешно зарегистрирован")
    @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
    ResponseEntity<ResponseDto<?>> registerNewUser(@RequestBody @Valid UserDto userDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Не верный пароль",
                    content = @Content)})
    @Operation(summary = "Вход пользователя по его email и паролю")
    ResponseEntity<ResponseDto<UserResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto);

    @Operation(summary = "Выход из приложения")
    ResponseEntity<ResponseDto<?>> logout();
    @Operation(summary = "Получение информации о текущем пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content)})
    ResponseEntity<ResponseDto<?>> getCurrentUserInfo();
}
