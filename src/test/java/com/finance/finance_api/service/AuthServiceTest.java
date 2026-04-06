package com.finance.finance_api.service;

import com.finance.finance_api.dto.AuthRequestDTO;
import com.finance.finance_api.dto.AuthResponseDTO;
import com.finance.finance_api.model.Role;
import com.finance.finance_api.model.User;
import com.finance.finance_api.repository.UserRepository;
import com.finance.finance_api.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenUsernameIsNew() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any())).thenReturn("testToken");

        // Act
        AuthResponseDTO response = authService.register("testuser", "password", Role.ROLE_ADMIN);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("testToken", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authService.register("testuser", "password", Role.ROLE_ADMIN)
        );
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO("testuser", "password", "ROLE_ADMIN");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any())).thenReturn("testToken");

        // Act
        AuthResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("testToken", response.getToken());
        verify(authenticationManager).authenticate(any());
    }
}
