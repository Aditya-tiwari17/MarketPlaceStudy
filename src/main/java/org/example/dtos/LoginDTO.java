package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    @NotNull(message = "username required")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotNull(message = "password required")
    @NotBlank(message = "password cannot be blank")
    private String password;
}
