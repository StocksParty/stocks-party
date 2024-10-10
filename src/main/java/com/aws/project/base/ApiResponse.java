package com.aws.project.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;

    // Overloaded constructor for responses without data
    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}
