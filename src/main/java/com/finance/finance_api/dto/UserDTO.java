package com.finance.finance_api.dto;

import com.finance.finance_api.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
    private boolean active;
}
