package com.finance.finance_api;

import com.finance.finance_api.model.Role;
import com.finance.finance_api.repository.UserRepository;
import com.finance.finance_api.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private String generateToken(String username, Role role) {
        if (!userRepository.existsByUsername(username)) {
            com.finance.finance_api.model.User user = com.finance.finance_api.model.User.builder()
                    .username(username)
                    .password("pass")
                    .role(role)
                    .active(true)
                    .build();
            userRepository.save(user);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtils.generateToken(userDetails);
    }

    @Test
    void whenViewerTriesToCreateRecord_thenForbidden() throws Exception {
        String token = generateToken("viewer_user_create", Role.ROLE_VIEWER);

        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 100.0, \"type\": \"INCOME\", \"category\": \"SALARY\", \"date\": \"2023-11-20\", \"description\": \"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAdminTriesToCreateRecord_thenOk() throws Exception {
        String token = generateToken("admin_user_create", Role.ROLE_ADMIN);

        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 100.0, \"type\": \"INCOME\", \"category\": \"SALARY\", \"date\": \"2023-11-20\", \"description\": \"Test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAnalystTriesToAccessDashboard_thenOk() throws Exception {
        String token = generateToken("analyst_user_dashboard", Role.ROLE_ANALYST);

        mockMvc.perform(get("/api/dashboard/summary")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void whenUnauthenticated_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isForbidden()); 
    }
}
