package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest implements Serializable {
    public String title;
    public String priority;
    public Long assignedTo;
    public Long assignedBy;
}
