package com.example.bookdahita.repository;

import com.example.bookdahita.models.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface InboxRepository extends JpaRepository<Inbox, Long> {
    List<Inbox> findAllByOrderByDateDesc();
}
