package com.example.bookdahita.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "iduser", nullable = false)
    private User user;

    @Column(name = "tsttotalmoney", nullable = false)
    private Integer totalMoney;

    @Column(name = "tstnote", nullable = false, length = 355)
    private String note;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inbox> inboxes = new HashSet<>();

    @Column(name = "status", nullable = false, length = 50, columnDefinition = "varchar(50) default 'Pending'")
    private String status;

    // Constructors
    public Transaction() {}

    public Transaction(User user, Integer totalMoney, String note) {
        this.user = user;
        this.totalMoney = totalMoney;
        this.note = note;
        this.orders = new HashSet<>();
        this.inboxes = new HashSet<>();
        this.status = "Pending"; // Đặt trạng thái mặc định là Pending
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Integer totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Inbox> getInboxes() {
        return inboxes;
    }

    public void setInboxes(Set<Inbox> inboxes) {
        this.inboxes = inboxes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}