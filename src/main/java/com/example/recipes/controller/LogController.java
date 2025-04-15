package com.example.recipes.controller;

import com.example.recipes.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Log controller", description = "API для работы с логами")
@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Формирует и возвращает лог-файл за указанную дату.
     *
     * @param date Дата в формате yyyy-MM-dd.
     * @return Лог-файл в виде ответа.
     * @throws IOException Если произошла ошибка при формировании лог-файла.
     */

    @Operation(
            summary = "Получить лог-файл за указанную дату",
            description = " Лог-файл формируется из общего файла с фильтрацией по указанной дате.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Лог-файл успешно сформирован и возвращён.",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный формат даты."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера при обработке файла."
                    )
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<Resource> getLogsForDate(@RequestParam String date) throws IOException {
        Resource logFile = logService.getLogFileForDate(date);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + logFile.getFilename() + "\"")
                .body(logFile);
    }
}
