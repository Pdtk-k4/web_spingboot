package com.example.bookdahita.repository;

import com.example.bookdahita.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);
    @Query("SELECT DATE(u.createdDate) as date, COUNT(u) as count " +
            "FROM User u WHERE u.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(u.createdDate)")
    List<Object[]> findUserRegistrationByDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CONCAT(YEAR(u.createdDate), '-', MONTH(u.createdDate)) as month, COUNT(u) as count " +
            "FROM User u WHERE u.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(u.createdDate), MONTH(u.createdDate)")
    List<Object[]> findUserRegistrationByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
