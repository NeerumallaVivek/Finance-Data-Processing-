package com.finance.finance_api.controller;

import com.finance.finance_api.dto.DashboardSummaryDTO;
import com.finance.finance_api.model.User;
import com.finance.finance_api.repository.UserRepository;
import com.finance.finance_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('ROLE_VIEWER', 'ROLE_ANALYST', 'ROLE_ADMIN')")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(dashboardService.getDashboardSummary(user));
    }
}
