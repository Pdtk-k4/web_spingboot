package com.example.bookdahita.controller.admin;

import com.example.bookdahita.dto.CategoryStatsDTO;
import com.example.bookdahita.dto.UserStatsDTO;
import com.example.bookdahita.repository.InboxRepository;
import com.example.bookdahita.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private InboxRepository inboxRepository;

    @GetMapping("/revenue-by-month")
    public Map<String, Object> getRevenueByMonth(@RequestParam("year") int year) {
        Map<String, Object> response = new HashMap<>();
        double[] monthlyRevenue = new double[12];

        // Initialize array with zeros
        Arrays.fill(monthlyRevenue, 0);

        // Fetch revenue data
        List<Object[]> results = inboxRepository.findRevenueByMonth(year);
        for (Object[] result : results) {
            int month = ((Number) result[0]).intValue();
            double revenue = ((Number) result[1]).doubleValue();
            monthlyRevenue[month - 1] = revenue;
        }

        response.put("monthlyRevenue", monthlyRevenue);
        return response;
    }

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