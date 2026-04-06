package com.finance.finance_api.controller;

import com.finance.finance_api.dto.RecordDTO;
import com.finance.finance_api.model.TransactionType;
import com.finance.finance_api.model.User;
import com.finance.finance_api.repository.UserRepository;
import com.finance.finance_api.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VIEWER', 'ROLE_ANALYST', 'ROLE_ADMIN')")
    public ResponseEntity<List<RecordDTO>> getAllRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        if (category != null || type != null || startDate != null || endDate != null) {
            return ResponseEntity.ok(recordService.filterRecords(user, category, type, startDate, endDate));
        }
        return ResponseEntity.ok(recordService.getAllRecords(user));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RecordDTO> createRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RecordDTO recordDTO
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(recordService.createRecord(user, recordDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RecordDTO> updateRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody RecordDTO recordDTO
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(recordService.updateRecord(user, id, recordDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        recordService.deleteRecord(user, id);
        return ResponseEntity.noContent().build();
    }
}
