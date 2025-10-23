package org.frogcy.furnitureadmin.dashboard.dto;

import java.util.Date;

public record NewCustomerDataPoint(
        Integer id,
        Date registrationDate,
        String fullName,
        String email,
        String avatarUrl,
        boolean verified // Thay đổi ở đây
) {}