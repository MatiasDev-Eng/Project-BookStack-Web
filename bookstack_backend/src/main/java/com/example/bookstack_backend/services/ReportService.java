package com.example.bookstack_backend.services;


import com.example.bookstack_backend.models.EActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    AuditLogService logService;

    public void reportBook(Long bookId) {
        logService.log(bookId, EActionType.REPORT, "Book has been reported: " + bookId);
    }
}
