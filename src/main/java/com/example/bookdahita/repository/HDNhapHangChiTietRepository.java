package com.example.bookdahita.repository;

import com.example.bookdahita.models.HDNhapHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HDNhapHangChiTietRepository extends JpaRepository<HDNhapHangChiTiet, Long> {
    List<HDNhapHangChiTiet> findByHdNhapHangId(String hdNhapHangId);
    Optional<HDNhapHangChiTiet> findTopByProductIdOrderByIdDesc(Long productId);
}
