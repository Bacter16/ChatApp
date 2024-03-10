package com.exemple.socialapp.domain;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long,Long>>{
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    private final Long senderId;
    private final Long receiverId;
    private final LocalDateTime requestDateTime;
    private Status status;

    public FriendRequest(long senderId, long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.requestDateTime = LocalDateTime.now();
        this.status = Status.PENDING;
        super.id = new Tuple<>(senderId,receiverId);
    }

    public FriendRequest(long senderId, long receiverId, LocalDateTime requestDateTime, Status status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.requestDateTime = requestDateTime;
        this.status = status;
        super.id = new Tuple<>(senderId,receiverId);
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", requestDateTime=" + requestDateTime +
                ", status=" + status +
                '}';
    }
}
