package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.DetailInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailInboxRepository extends JpaRepository<DetailInbox, Long> {
}