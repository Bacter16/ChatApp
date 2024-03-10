package com.exemple.socialapp.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message{

    private final Long reply_to_message_id;

    public ReplyMessage(Long message_id, Long from_id, List<Long> to, String message, LocalDateTime date, Long reply_to_message_id){
        super(message_id, from_id, to, message, date);
        this.reply_to_message_id = reply_to_message_id;
    }

    public ReplyMessage(Long from_id, List<Long> to, String message, Long reply_to_message_id) {
        super(from_id, to, message);
        this.reply_to_message_id = reply_to_message_id;
    }

    public Long get_reply_to_message_id() {
        return reply_to_message_id;
    }
}
