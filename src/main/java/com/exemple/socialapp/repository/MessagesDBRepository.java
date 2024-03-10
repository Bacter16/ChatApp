package com.exemple.socialapp.repository;

import com.exemple.socialapp.domain.Message;
import com.exemple.socialapp.domain.ReplyMessage;
import com.exemple.socialapp.util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessagesDBRepository implements Repository<Long, Message>{

    public MessagesDBRepository() {}

    @Override
    public Optional<Message> findOne(Long longID) {
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement("select  * from messages where message_id = ?")
        ) {
            statement.setLong(1,longID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                long message_id_var = resultSet.getLong("message_id");
                long from_id = resultSet.getLong("from_user_id");
                String message = resultSet.getString("message");
                Timestamp date = resultSet.getTimestamp("message_data");
                LocalDateTime localDateTime = date.toLocalDateTime();
                long reply_to_message_id = resultSet.getLong("reply_to_message_id");
                List<Long> lista = new ArrayList<>();
                PreparedStatement statement1 = connection.prepareStatement("select  * from message_to_users where message_id = ?");
                statement1.setLong(1,longID);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                    Long user_id= resultSet1.getLong("to_user_id");
                    lista.add(user_id);
                }
                Message m;
                if(reply_to_message_id == -1){
                    m = new Message(message_id_var, from_id, lista, message, localDateTime);
                } else {
                    m = new ReplyMessage(message_id_var, from_id, lista, message, localDateTime, reply_to_message_id);
                }
                return Optional.of(m);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long message_id_var = resultSet.getLong("message_id");
                long from_id = resultSet.getLong("from_user_id");
                String message = resultSet.getString("message");
                long reply_to_message_id = resultSet.getLong("reply_to_message_id");

                boolean isReplyToMessageNull = resultSet.wasNull();

                Timestamp date = resultSet.getTimestamp("message_data");
                LocalDateTime localDateTime = date.toLocalDateTime();

                List<Long> lista = new ArrayList<>();
                PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM message_to_users WHERE message_id = ?");
                statement1.setLong(1, message_id_var);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    Long user_id = resultSet1.getLong("to_user_id");
                    lista.add(user_id);
                }

                Message m;
                if (isReplyToMessageNull) {
                    m = new Message(message_id_var, from_id, lista, message, localDateTime);
                } else {
                    m = new ReplyMessage(message_id_var, from_id, lista, message, localDateTime, reply_to_message_id);
                }

                messages.add(m);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> save(Message entity) {
        try {
            var connection = DatabaseConnector.getConnection();

            String selectSQL;
            if (entity instanceof ReplyMessage) {
                selectSQL = "insert into messages (from_user_id, message, message_data, reply_to_message_id) values(?,?,?,?)";
            }else{
                selectSQL = "insert into messages (from_user_id, message, message_data) values(?,?,?)";
            }

            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setLong(1, entity.getFrom_id());
            statement.setString(2, entity.getMessage());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            if (entity instanceof ReplyMessage) {
                statement.setLong(4, ((ReplyMessage) entity).get_reply_to_message_id());
            }

            if(statement.executeUpdate() == 0){
                return Optional.empty();
            }
            PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM messages WHERE message = ? AND message_data = ?");
            statement2.setString(1, entity.getMessage());
            statement2.setTimestamp(2, Timestamp.valueOf(entity.getDate()));
            ResultSet resultSet = statement2.executeQuery();
            long id = -1;
            if(resultSet.next()) {
                id = resultSet.getLong("message_id");
            }
            PreparedStatement statement1 = connection.prepareStatement("insert into message_to_users (message_id, to_user_id) values (?, ?)");
            for(var elem : entity.getTo()){
                statement1.setLong(1,id);
                statement1.setLong(2,elem);
                if(statement1.executeUpdate() == 0){
                    return Optional.empty();
                }
            }

            return Optional.of(entity);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        if (aLong == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try{
            var connection = DatabaseConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement("delete from messages where message_id=?");
            statement.setLong(1, aLong);
            Optional<Message> foundMessage = findOne(aLong);
            int response = 0;
            if (foundMessage.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : foundMessage;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }

        try (Connection connection = DatabaseConnector.getConnection()) {
            Optional<Message> existingMessage = findOne(entity.getId());

            if (existingMessage.isPresent()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE messages SET message = ? WHERE message_id = ?");
                statement.setString(1, entity.getMessage());
                statement.setLong(2, entity.getId());

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    return Optional.of(entity);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
