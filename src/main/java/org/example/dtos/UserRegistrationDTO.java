package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.RoleEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    @NotNull(message = "username required")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotNull(message = "password required")
    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotNull(message = "email required")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotNull(message = "role required")
    private RoleEnum role; // "POSTER" or "BIDDER"
}