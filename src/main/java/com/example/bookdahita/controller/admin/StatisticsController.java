package com.example.bookdahita.controller.admin;

import com.example.bookdahita.dto.CategoryStatsDTO;
import com.example.bookdahita.dto.UserStatsDTO;
import com.example.bookdahita.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/products-by-category")
    public List<CategoryStatsDTO> getProductCountByCategory() {
        return statisticsService.getProductCountByCategory();
    }

    @GetMapping("/users/day")
    public List<UserStatsDTO> getUserRegistrationByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return statisticsService.getUserRegistrationByDay(startDate, endDate);
    }

    @GetMapping("/users/month")
    public List<UserStatsDTO> getUserRegistrationByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return statisticsService.getUserRegistrationByMonth(startDate, endDate);
    }
}