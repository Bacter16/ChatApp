package com.exemple.socialapp.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    private final long u1;
    private final long u2;

    public Friendship(long utilizator1, long utilizator2) {
        u1 = utilizator1;
        u2 = utilizator2;
        date = LocalDateTime.now();
    }

    public Friendship(long utilizator1, long utilizator2, LocalDateTime data) {
        u1 = utilizator1;
        u2 = utilizator2;
        date = data;
    }

    public long getU1() {
        return u1;
    }

    public long getU2() {
        return u2;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "date=" + date +
                ", u1=" + u1 +
                ", u2=" + u2 +
                '}';
    }
}
