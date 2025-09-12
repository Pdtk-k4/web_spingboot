package com.example.bookdahita.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "hdnhaphang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HDNhapHang {
    @Id
    private String id;

    @Column
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "iduser")
    private User user;

    @Column(length = 100, nullable = false)
    private String status;

    @OneToMany(mappedBy = "hdNhapHang", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<HDNhapHangChiTiet> hdNhapHangChiTiets = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(id, createAt, status, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HDNhapHang that = (HDNhapHang) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createAt, that.createAt) &&
                Objects.equals(status, that.status) &&
                Objects.equals(user, that.user);
    }
}