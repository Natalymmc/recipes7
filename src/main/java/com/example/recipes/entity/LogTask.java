package com.example.recipes.entity;

public class LogTask {

    private Long id;
    private String date;
    private String status; // IN_PROGRESS, COMPLETED, FAILED
    private String filePath;
    private String errorMessage;

    public LogTask(Long id, String date) {
        this.id = id;
        this.date = date;
        this.status = "IN_PROGRESS";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
