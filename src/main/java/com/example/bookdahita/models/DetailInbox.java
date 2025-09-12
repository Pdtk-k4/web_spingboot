package com.example.bookdahita.models;

import jakarta.persistence.*;

@Entity
@Table(name = "tbldetailinbox")
public class DetailInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idpro", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "iduser", nullable = false)
    private User user;

    @Column(name = "idib", nullable = false)
    private Long inboxId;

    @Column(name = "odquantity", nullable = false)
    private Integer quantity;

    // Constructors
    public DetailInbox() {}

    public DetailInbox(Product product, User user, Long inboxId, Integer quantity) {
        this.product = product;
        this.user = user;
        this.inboxId = inboxId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getInboxId() {
        return inboxId;
    }

    public void setInboxId(Long inboxId) {
        this.inboxId = inboxId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
