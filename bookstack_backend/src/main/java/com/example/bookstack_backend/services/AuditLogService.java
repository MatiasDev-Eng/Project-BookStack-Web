package com.example.bookstack_backend.services;

import com.example.bookstack_backend.models.AuditLog;
import com.example.bookstack_backend.models.EActionType;
import com.example.bookstack_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long userId, EActionType action, String details) {
        auditLogRepository.save(new AuditLog(userId, action, details));
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getLogsByAction(EActionType action) {
        return auditLogRepository.findByAction(action);
    }
}
