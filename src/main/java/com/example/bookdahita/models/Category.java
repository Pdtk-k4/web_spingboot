package com.example.bookdahita.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "tbl_category")
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "catname")
    private String catname;

    @Column(name = "catstatus")
    private Boolean catstatus;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private Set<Product> products;

    public Category() {
    }

    public Category(Long id, String catname, Boolean catstatus, Set<Product> products) {
        this.id = id;
        this.catname = catname;
        this.catstatus = catstatus;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public Boolean getCatstatus() {
        return catstatus;
    }

    public void setCatstatus(Boolean catstatus) {
        this.catstatus = catstatus;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
