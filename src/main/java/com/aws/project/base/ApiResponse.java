package com.aws.project.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper.
 *
 * @param <T> the type of the response data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;

    // Constructor for responses without data
    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}
