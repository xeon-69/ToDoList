package com.xeon.todolist.entity;

import com.xeon.todolist.enums.TaskPriority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tasks extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne // foreign key
    @JoinColumn(name = "user_id") // changing name
    private Users user;
    private String title;
    private boolean isCompleted;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;
    private Integer priorityWeight;

    @PrePersist //before insert
    @PreUpdate //before update
    public void syncPriorityWeight(){
        // if there is a priority, auto assign the priority weight value
        if(priority != null){
            this.priorityWeight = priority.getWeight();
        }
    }

    // createdAt, updatedAt will be included by the super (parent) class
}
