package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.Admin;
import com.example.bookstack_backend.models.AuditLog;
import com.example.bookstack_backend.models.EActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByTimestampDesc();

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(EActionType action);
}
