package com.example.recipes.service;

import com.example.recipes.entity.LogTask;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LogTaskService {

    private final LogService logService;
    private final ConcurrentHashMap<Long, LogTask> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong taskIdGenerator = new AtomicLong(1);

    public LogTaskService(LogService logService) {
        this.logService = logService;
    }

    @Async
    public CompletableFuture<Long> createLogFileAsync(String date) {
        Long taskId = taskIdGenerator.getAndIncrement();
        LogTask task = new LogTask(taskId, date);
        taskStore.put(taskId, task);

        // Асинхронное выполнение задачи
        CompletableFuture.runAsync(() -> {
            try {
                  Thread.sleep(6000);

                File logFile = logService.createLogFileForDate(date);
                task.setStatus("COMPLETED");
                task.setFilePath(logFile.getAbsolutePath());
            } catch (IOException e) {
                task.setStatus("FAILED");
                task.setErrorMessage("Failed to create log file: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                task.setStatus("FAILED");
                task.setErrorMessage("Task interrupted: " + e.getMessage());
            }
        });

        return CompletableFuture.completedFuture(taskId);
    }

    public LogTask getTaskStatus(Long taskId) {
        LogTask task = taskStore.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task ID not found.");
        }
        return task;
    }

    public ResponseEntity<Resource> getLogFile(Long taskId) {
        LogTask task = taskStore.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task ID not found");
        }

        if (!"COMPLETED".equals(task.getStatus())) {
            throw new IllegalStateException("File is not yet available");
        }

        try {
            Resource resource = logService.getLogFileForDate(task.getDate());
            return ResponseEntity.ok(resource);
        } catch (IOException e) {
            throw new RuntimeException("Error fetching file: " + e.getMessage());
        }
    }
}
