package com.example.bookdahita.repository;

import com.example.bookdahita.models.Inbox;
import com.example.bookdahita.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, Long> {
    List<Inbox> findAllByOrderByDateDesc();

    @Query("SELECT MONTH(i.date) as month, SUM(i.price) as revenue " +
            "FROM Inbox i " +
            "WHERE i.status = true AND YEAR(i.date) = :year " +
            "GROUP BY MONTH(i.date) " +
            "ORDER BY MONTH(i.date)")
    List<Object[]> findRevenueByMonth(int year);

    // Thêm phương thức để tìm Inbox theo User
    List<Inbox> findByUser(User user);
}