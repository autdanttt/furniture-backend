package org.frogcy.furnitureadmin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueOrderStatsDTO {
    private int month;
    private long revenue;
    private long orders;
}