package com.example.bookdahita.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hdnhaphangct")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HDNhapHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maHDNhapHang")
    public HDNhapHang hdNhapHang;

    @ManyToOne
    @JoinColumn(name = "product_id")
    public Product product;

    @Column(name = "proimage")
    private String proimage;

    @Column
    private Integer quantity;

    @Column
    private Integer importPrice;

    @ManyToOne
    @JoinColumn(name = "idsup")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "idau")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "idcat")
    private Category category;


}
