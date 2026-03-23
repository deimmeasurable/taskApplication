package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskRequest implements Serializable {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Priority is required")
    private String priority; // High, Medium, Low

    @NotNull(message = "Assignee ID is required")
    private Long assignedTo;
    public Long assignedBy;
}
