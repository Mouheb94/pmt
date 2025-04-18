package com.rendu.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean read = false;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sent_date")
    private LocalDate sentDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Task_id")
    private Task task;

    public Notification() {
    }

    public Notification(String message, User user) {
        this.message = message;
        this.user = user;
    }



}
