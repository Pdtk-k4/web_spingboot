package com.example.bookdahita.models;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_productsimages")
public class ProductsImages {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "piimage")
    private String piimage;

    @ManyToOne
    @JoinColumn(name = "idpro", referencedColumnName = "id")
    private Product product;

    public ProductsImages() {
    }

    public ProductsImages(Long id, String piimage, Product product) {
        this.id = id;
        this.piimage = piimage;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPiimage() {
        return piimage;
    }

    public void setPiimage(String piimage) {
        this.piimage = piimage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
