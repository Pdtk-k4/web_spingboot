package com.example.bookdahita.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_products")
@Data
@Builder
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proname")
    private String proname;

    @Column(name = "proimage")
    private String proimage;

    @Column(name = "proprice")
    private Integer proprice;

    @Column(name = "procontent", length = 4000)
    private String procontent;

    @Column(name = "prosale")
    private Integer prosale;

    @Column(name = "pronewbook")
    private Boolean pronewbook;

    @Column(name = "prostatus")
    private Boolean prostatus;

    @ManyToOne
    @JoinColumn(name = "idcat", referencedColumnName = "id")
    @JsonBackReference(value = "category-product")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "idsup", referencedColumnName = "id")
    @JsonBackReference(value = "supplier-product")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "idau", referencedColumnName = "id")
    @JsonBackReference(value = "author-product")
    private Author author;

    @OneToMany(mappedBy = "product")
    private Set<ProductsImages> productsImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HDNhapHangChiTiet> nhapHangChiTiets = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inventory> inventories = new HashSet<>();

    public Product() {
    }

    public Product(Long id, String proname, String proimage, Integer proprice, String procontent, Integer prosale, Boolean pronewbook, Boolean prostatus, Category category, Supplier supplier, Author author, Set<ProductsImages> productsImages, Set<HDNhapHangChiTiet> nhapHangChiTiets, Set<Inventory> inventories) {
        this.id = id;
        this.proname = proname;
        this.proimage = proimage;
        this.proprice = proprice;
        this.procontent = procontent;
        this.prosale = prosale;
        this.pronewbook = pronewbook;
        this.prostatus = prostatus;
        this.category = category;
        this.supplier = supplier;
        this.author = author;
        this.productsImages = productsImages;
        this.nhapHangChiTiets = nhapHangChiTiets;
        this.inventories = inventories;
    }

    public Set<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(Set<Inventory> inventories) {
        this.inventories = inventories;
    }

    public Set<HDNhapHangChiTiet> getNhapHangChiTiets() {
        return nhapHangChiTiets;
    }

    public void setNhapHangChiTiets(Set<HDNhapHangChiTiet> nhapHangChiTiets) {
        this.nhapHangChiTiets = nhapHangChiTiets;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }

    public String getProimage() {
        return proimage;
    }

    public void setProimage(String proimage) {
        this.proimage = proimage;
    }

    public Integer getProprice() {
        return proprice;
    }

    public void setProprice(Integer proprice) {
        this.proprice = proprice;
    }

    public String getProcontent() {
        return procontent;
    }

    public void setProcontent(String procontent) {
        this.procontent = procontent;
    }

    public Integer getProsale() {
        return prosale;
    }

    public void setProsale(Integer prosale) {
        this.prosale = prosale;
    }

    public Boolean getPronewbook() {
        return pronewbook;
    }

    public void setPronewbook(Boolean pronewbook) {
        this.pronewbook = pronewbook;
    }

    public Boolean getProstatus() {
        return prostatus;
    }

    public void setProstatus(Boolean prostatus) {
        this.prostatus = prostatus;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<ProductsImages> getProductsImages() {
        return productsImages;
    }

    public void setProductsImages(Set<ProductsImages> productsImages) {
        this.productsImages = productsImages;
    }
}
