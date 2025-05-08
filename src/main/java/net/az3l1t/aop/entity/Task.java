package net.az3l1t.aop.entity;

import jakarta.persistence.*;
import lombok.*;
import net.az3l1t.aop.entity.enumirations.TaskStatus;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
}
