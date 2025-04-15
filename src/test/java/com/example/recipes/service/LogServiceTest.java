package com.example.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class LogServiceTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private LogService logService;

@BeforeEach
void setup() throws IOException {
    tempDir = Files.createTempDirectory("tempDir");
    Path logDirPath = tempDir.resolve("logs/");
    logDirPath.toFile().mkdirs(); // Создаём временную директорию
    logService = new LogService(logDirPath.toString());
}


    private static final String LOG_DIRECTORY = "logs/";
    private static final String COMMON_LOG_FILE = "application.log";
    //private final LogService logService = new LogService(tempDir.toString());

    @Test
    void createLogFileForDate_success() throws IOException {
        // Arrange
        String date = "2023-12-01";
        Path logDirPath = tempDir.resolve("logs/");
        logDirPath.toFile().mkdirs(); // Создаем временную директорию

        // Инициализация LogService с временной директорией
        LogService logService = new LogService(logDirPath.toString());

        File commonLogFile = logDirPath.resolve("application.log").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(commonLogFile))) {
            writer.write("2023-12-01 Log entry 1");
            writer.newLine();
            writer.write("2023-12-02 Log entry 2");
            writer.newLine();
        }

        File expectedLogFile = logDirPath.resolve("application.log.2023-12-01.log").toFile();

        // Act
        File resultLogFile = logService.createLogFileForDate(date);

        // Assert
        assertTrue(resultLogFile.exists());
        assertEquals(expectedLogFile.getName(), resultLogFile.getName());
        try (BufferedReader reader = new BufferedReader(new FileReader(resultLogFile))) {
            String firstLine = reader.readLine();
            assertEquals("2023-12-01 Log entry 1", firstLine);
            assertNull(reader.readLine()); // Должна быть только одна строка
        }
    }


    @Test
    void createLogFileForDate_commonLogFileNotFound_throwsIOException() {
        // Arrange
        String date = "2023-12-01";
        Path logDirPath = tempDir.resolve(LOG_DIRECTORY);
        logDirPath.toFile().mkdirs(); // Создаем временную директорию без файла

        // Act & Assert
        IOException exception = assertThrows(IOException.class,
                () -> logService.createLogFileForDate(date));
        assertTrue(exception.getMessage().contains("Common log file not found"));
    }

    @Test
    void createLogFileForDate_noLogsForDate_createsEmptyFile() throws IOException {
        // Arrange
        String date = "2023-12-03"; // Дата, которая отсутствует в логе
        Path logDirPath = tempDir.resolve(LOG_DIRECTORY);
        logDirPath.toFile().mkdirs(); // Создаем временную директорию

        File commonLogFile = logDirPath.resolve(COMMON_LOG_FILE).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(commonLogFile))) {
            writer.write("2023-12-01 Log entry 1");
            writer.newLine();
            writer.write("2023-12-02 Log entry 2");
            writer.newLine();
        }

        File expectedLogFile = logDirPath.resolve("application.log.2023-12-03.log").toFile();

        // Act
        File resultLogFile = logService.createLogFileForDate(date);

        // Assert
        assertTrue(resultLogFile.exists());
        assertEquals(expectedLogFile.getName(), resultLogFile.getName());
        assertEquals(0, resultLogFile.length()); // Файл должен быть пустым
    }

    @Test
    void getLogFileForDate_success() throws IOException {
        // Arrange
        String date = "2023-12-01";
        Path logDirPath = tempDir.resolve(LOG_DIRECTORY);
        logDirPath.toFile().mkdirs(); // Создаем временную директорию

        File commonLogFile = logDirPath.resolve(COMMON_LOG_FILE).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(commonLogFile))) {
            writer.write("2023-12-01 Log entry 1");
            writer.newLine();
            writer.write("2023-12-02 Log entry 2");
            writer.newLine();
        }

        // Act
        Resource resource = logService.getLogFileForDate(date);

        // Assert
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.getURI().toString().endsWith("application.log.2023-12-01.log"));
    }

}

