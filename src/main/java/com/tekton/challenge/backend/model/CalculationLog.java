package com.tekton.challenge.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    private String endpoint;

    private String request;

    @Column(columnDefinition = "TEXT")
    private String response;

    private boolean success;

    public CalculationLog(LocalDateTime timestamp, String endpoint, String request, String response, boolean success) {
        this.timestamp = timestamp;
        this.endpoint = endpoint;
        this.request = request;
        this.response = response;
        this.success = success;
    }
}
