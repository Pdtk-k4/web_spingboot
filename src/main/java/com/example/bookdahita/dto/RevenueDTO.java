package com.example.bookdahita.dto;

public class RevenueDTO {
    private String period; // Ngày (VD: "2025-05-22") hoặc tháng (VD: "2025-05")
    private Double revenue;

    public RevenueDTO(String period, Double revenue) {
        this.period = period;
        this.revenue = revenue;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}