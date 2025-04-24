package com.example.recipes.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {

    private final Map<String, Integer> visitCounts = new ConcurrentHashMap<>();

    // Метод для увеличения счётчика
    public synchronized void incrementVisitCount(String url) {
        visitCounts.compute(url, (key, value) -> value == null ? 1 : value + 1);
    }

    // Метод для получения счётчика определённого URL
    public synchronized int getVisitCount(String url) {
        return visitCounts.getOrDefault(url, 0);
    }

    // Метод для получения общего количества посещений
    public synchronized int getTotalVisitCount() {
        return visitCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Метод для получения всех счётчиков
    public synchronized Map<String, Integer> getAllVisitCounts() {
        return new ConcurrentHashMap<>(visitCounts);
    }
}
