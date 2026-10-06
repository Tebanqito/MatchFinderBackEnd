package com.desarrolloweb.matchfinder.services;

import com.desarrolloweb.matchfinder.dtos.request.LoginRequest;
import com.desarrolloweb.matchfinder.dtos.request.RegistroRequest;
import com.desarrolloweb.matchfinder.dtos.response.AuthResponse;

public interface AuthService {

    AuthResponse registrar(RegistroRequest request);

    AuthResponse registrarOwner(RegistroRequest request, String claveOwner);

    AuthResponse login(LoginRequest request);
}
