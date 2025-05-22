package com.example.bookdahita.repository;

import com.example.bookdahita.models.DetailInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailInboxRepository extends JpaRepository<DetailInbox, Long> {
    List<DetailInbox> findByInboxId(Long inboxId);
}