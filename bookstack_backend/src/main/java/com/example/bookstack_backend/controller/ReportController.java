package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/book/{bookId}")
    public ResponseEntity<?> reportBook(@PathVariable Long bookId) {
        reportService.reportBook(bookId);
        return ResponseEntity.ok().build();
    }

}
