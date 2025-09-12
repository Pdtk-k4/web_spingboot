package com.example.bookdahita.models;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_role")
public class Role {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "namerole")
    private String nameRole;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private Set<UserRole> rolesUser;

    public Role() {
    }

    public Role(Long id, String nameRole, Set<UserRole> rolesUser) {
        this.id = id;
        this.nameRole = nameRole;
        this.rolesUser = rolesUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameRole() {
        return nameRole;
    }

    public void setNameRole(String nameRole) {
        this.nameRole = nameRole;
    }

    public Set<UserRole> getRolesUser() {
        return rolesUser;
    }

    public void setRolesUser(Set<UserRole> rolesUser) {
        this.rolesUser = rolesUser;
    }
}