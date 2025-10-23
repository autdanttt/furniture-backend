package org.frogcy.furnitureadmin.dashboard.dto;

public enum StatsPeriod {
    THIS_WEEK,
    LAST_WEEK,

    // Theo tháng (giữ lại và đổi tên cho nhất quán)
    THIS_MONTH, // thay cho CURRENT_MONTH
    LAST_MONTH,

    // Theo quý
    THIS_QUARTER,
    LAST_QUARTER,

    // Theo năm
    THIS_YEAR,
    LAST_YEAR
}
