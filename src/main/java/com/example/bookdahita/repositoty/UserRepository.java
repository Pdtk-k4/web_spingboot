package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);
}
