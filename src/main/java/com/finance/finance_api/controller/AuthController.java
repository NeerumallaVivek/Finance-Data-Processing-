package com.finance.finance_api.controller;

import com.finance.finance_api.dto.AuthRequestDTO;
import com.finance.finance_api.dto.AuthResponseDTO;
import com.finance.finance_api.model.Role;
import com.finance.finance_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody AuthRequestDTO request) {
        Role role = Role.ROLE_VIEWER; // Default
        if (request.getRole() != null) {
            String roleStr = request.getRole().toUpperCase().trim();
            if (!roleStr.startsWith("ROLE_")) {
                roleStr = "ROLE_" + roleStr;
            }
            try {
                role = Role.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                // Keep default ROLE_VIEWER if role name is invalid
            }
        }
        return ResponseEntity.ok(authService.register(request.getUsername(), request.getPassword(), role));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
