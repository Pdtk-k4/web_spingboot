package com.example.bookdahita.dto;

public class UserStatsDTO {
    private String period;
    private Long count;

    public UserStatsDTO(String period, Long count) {
        this.period = period;
        this.count = count;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}