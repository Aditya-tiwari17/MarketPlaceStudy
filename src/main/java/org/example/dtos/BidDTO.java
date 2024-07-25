package org.example.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidDTO {
    @NotNull(message = "amount required")
    private Double amount;

    @NotNull(message = "jobId required")
    private Long jobId;

    @NotNull(message = "bidderId required")
    private Long bidderId;
}
