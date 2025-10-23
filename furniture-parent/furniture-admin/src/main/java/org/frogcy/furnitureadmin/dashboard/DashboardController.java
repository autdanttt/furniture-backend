package org.frogcy.furnitureadmin.dashboard;

import org.frogcy.furnitureadmin.dashboard.dto.DashboardSummaryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        DashboardSummaryDTO response = dashboardService.getSummary();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
