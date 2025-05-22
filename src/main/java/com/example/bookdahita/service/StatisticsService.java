package com.example.bookdahita.service;

import com.example.bookdahita.dto.CategoryStatsDTO;
import com.example.bookdahita.dto.UserStatsDTO;
import com.example.bookdahita.repository.ProductRepository;
import com.example.bookdahita.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public List<CategoryStatsDTO> getProductCountByCategory() {
        return productRepository.findProductCountByCategory().stream()
                .map(obj -> new CategoryStatsDTO((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    public List<UserStatsDTO> getUserRegistrationByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.findUserRegistrationByDay(startDate, endDate).stream()
                .map(obj -> new UserStatsDTO(obj[0].toString(), ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    public List<UserStatsDTO> getUserRegistrationByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.findUserRegistrationByMonth(startDate, endDate).stream()
                .map(obj -> new UserStatsDTO(obj[0].toString(), ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }
}