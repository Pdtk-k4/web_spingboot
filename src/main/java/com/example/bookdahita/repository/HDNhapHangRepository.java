package com.example.bookdahita.repository;

import com.example.bookdahita.models.HDNhapHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HDNhapHangRepository extends JpaRepository<HDNhapHang,Long> {
    boolean existsById(String id);

    Optional<HDNhapHang> findById(String id);

    Optional<HDNhapHang> findFirstByStatus(String status);
}
