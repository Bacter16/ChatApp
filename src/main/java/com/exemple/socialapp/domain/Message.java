package com.exemple.socialapp.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private final Long from_id;
    private List<Long> to;
    private String message;
    private LocalDateTime date;

    public Message(Long from_id, List<Long> to, String message) {
        this.from_id = from_id;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
    }

    public Message(Long message_id, Long from_id, List<Long> to, String message, LocalDateTime date) {
        super.id = message_id;
        this.from_id = from_id;
        this.to = to;
        this.message = message;
        this.date = date;
    }

    public Long getFrom_id() {
        return from_id;
    }

    public List<Long> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return message;
    }
}
