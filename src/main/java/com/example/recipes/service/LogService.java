package com.example.recipes.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final String logDirectory;
    private final String commonLogFileName;

    public LogService(@Value("${log.directory:/logs/}")String logDirectory) {
        this.logDirectory = logDirectory;
        this.commonLogFileName = "application.log";
    }

    public File createLogFileForDate(String date) throws IOException {
        Path commonLogPath = Paths.get(logDirectory, commonLogFileName);
        File commonLogFile = commonLogPath.toFile();

        // Проверяем, существует ли общий лог-файл
        if (!commonLogFile.exists()) {
            throw new IOException("Common log file not found: " + commonLogPath.toAbsolutePath());
        }

        // Имя нового лог-файла
        String newLogFileName = String.format("application.log.%s.log", date);
        File newLogFile = new File(logDirectory, newLogFileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(commonLogFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(newLogFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Фильтруем строки, содержащие указанную дату
                if (line.startsWith(date)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        return newLogFile;
    }


    public Resource getLogFileForDate(String date) throws IOException {
        File logFile = createLogFileForDate(date);

        if (!logFile.exists()) {
            throw new IOException("Log file not created for date: " + date);
        }

        return new UrlResource(logFile.toURI());
    }

}
