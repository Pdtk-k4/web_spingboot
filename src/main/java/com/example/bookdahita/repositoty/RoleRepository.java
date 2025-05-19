package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByNameRole(String nameRole);
}