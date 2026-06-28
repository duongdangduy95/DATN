package com.example.iotbackend.dto;

import lombok.Data;

@Data
public class SpeechToTextResponse {
    private boolean success;
    private String text;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
