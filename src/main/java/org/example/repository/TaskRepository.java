package org.example.repository;

import org.example.domain.Status;
import org.example.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


        Page<Task> findByAssignedToAndStatus(Long assignedTo, Status status, Pageable pageable);

        Page<Task> findByAssignedTo(Long assignedTo, Pageable pageable);

        Page<Task> findByStatus(Status status, Pageable pageable);

}
