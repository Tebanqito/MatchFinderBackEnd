package com.desarrolloweb.matchfinder.dtos.response;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String email,
        String rol
) { }
