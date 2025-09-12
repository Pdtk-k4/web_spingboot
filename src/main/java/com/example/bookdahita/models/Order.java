package com.example.bookdahita.models;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idtst", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "idpro", nullable = false)
    private Product product;

    @Column(name = "odquantity", nullable = false)
    private Integer quantity;

    @Column(name = "odprice", nullable = false)
    private Integer price;

    // Constructors
    public Order() {}

    public Order(Transaction transaction, Product product, Integer quantity, Integer price) {
        this.transaction = transaction;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}