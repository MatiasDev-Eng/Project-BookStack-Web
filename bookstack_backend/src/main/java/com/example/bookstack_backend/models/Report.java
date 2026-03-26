package com.example.bookstack_backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId; // Matches BIGINT/BIGSERIAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false) // Links to users(id)
    private User reporter;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation; // Matches text

    @Column(name = "date_reported", updatable = false)
    private LocalDateTime dateReported; // Matches timestamp

    @Column(name = "is_resolved")
    private Boolean isResolved; // Matches boolean

    public Report() {
        this.dateReported = LocalDateTime.now();
        this.isResolved = false;
    }

    // --- Constructor for new reports ---
    public Report(User reporter, String explanation) {
        this();
        this.reporter = reporter;
        this.explanation = explanation;
    }

    // --- Getters and Setters ---
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public LocalDateTime getDateReported() { return dateReported; }
    public void setDateReported(LocalDateTime dateReported) { this.dateReported = dateReported; }

    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
}