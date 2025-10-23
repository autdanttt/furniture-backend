package org.frogcy.furnitureadmin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStatsDTO {
    private String period; // ví dụ "2025-10-01" hoặc "2025-10"
    private Long totalRevenue;
}
