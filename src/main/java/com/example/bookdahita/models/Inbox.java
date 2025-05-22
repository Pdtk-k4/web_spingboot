package com.example.bookdahita.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_inbox")
public class Inbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idtst")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "iduser", nullable = false)
    private User user;

    @Column(name = "ibdate", nullable = false)
    private LocalDateTime date;

    @Column(name = "ibquantity", nullable = false)
    private Integer quantity;

    @Column(name = "ibprice", nullable = false)
    private Integer price;

    @Column(name = "cusname", nullable = false, length = 255)
    private String customerName;

    @Column(name = "cusaddress", nullable = false, length = 355)
    private String customerAddress;

    @Column(name = "cusphone", nullable = false, length = 255)
    private String customerPhone;

    @Column(name = "ibstatus", nullable = false)
    private Boolean status;

    // Constructors
    public Inbox() {
        this.date = LocalDateTime.now();
    }

    public Inbox(Transaction transaction, User user, Integer quantity, Integer price, String customerName, String customerAddress, String customerPhone, Boolean status) {
        this.transaction = transaction;
        this.user = user;
        this.quantity = quantity;
        this.price = price;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.status = status;
        this.date = LocalDateTime.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}