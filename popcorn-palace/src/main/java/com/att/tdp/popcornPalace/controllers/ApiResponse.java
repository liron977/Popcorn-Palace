package com.att.tdp.popcornPalace.controllers;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private T data;

    public ApiResponse(T data) {
        this.data = data;

    }

    // Getters and setters
}