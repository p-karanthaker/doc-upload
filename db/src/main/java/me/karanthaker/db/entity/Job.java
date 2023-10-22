package me.karanthaker.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String documentName;
    private UUID documentKey;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String notes;

    public enum Status {
        QUEUED,
        SCANNING,
        FAILED_SCAN,
        PENDING_APPROVAL,
        APPROVED,
        REJECTED
    }
}
