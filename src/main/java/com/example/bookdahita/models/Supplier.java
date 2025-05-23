package com.example.bookdahita.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "tbl_supplier")
public class Supplier {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supname")
    private String supname;

    @OneToMany(mappedBy = "supplier")
    @JsonManagedReference(value = "supplier-product")
    private Set<Product> products;

    public Supplier() {
    }

    public Supplier(Long id, String supname, Set<Product> products) {
        this.id = id;
        this.supname = supname;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupname() {
        return supname;
    }

    public void setSupname(String supname) {
        this.supname = supname;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
