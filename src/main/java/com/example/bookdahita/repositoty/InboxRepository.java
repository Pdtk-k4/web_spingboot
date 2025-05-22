package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.Inbox;
import com.example.bookdahita.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, Long> {
    List<Inbox> findByUserOrderByDateDesc(User user);
}
