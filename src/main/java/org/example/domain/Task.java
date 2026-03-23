package org.example.domain;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="m-task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long assignedTo;
    private Long assignedBy;

    private LocalDateTime createdAt;
}
