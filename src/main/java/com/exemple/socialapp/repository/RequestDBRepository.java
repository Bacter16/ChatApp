package com.exemple.socialapp.repository;


import com.exemple.socialapp.domain.FriendRequest;
import com.exemple.socialapp.domain.Tuple;
import com.exemple.socialapp.util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestDBRepository implements Repository<Tuple<Long, Long>, FriendRequest> {

    public RequestDBRepository() {}

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM friend_requests WHERE sender_id = ? AND receiver_id = ?")) {

            preparedStatement.setLong(1, id.getLeft());
            preparedStatement.setLong(2, id.getRight());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long senderId = resultSet.getLong("sender_id");
                    long receiverId = resultSet.getLong("receiver_id");
                    LocalDateTime requestDateTime = resultSet.getTimestamp("request_datetime").toLocalDateTime();
                    String statusString = resultSet.getString("status");
                    FriendRequest.Status status = FriendRequest.Status.valueOf(statusString);

                    return Optional.of(new FriendRequest(senderId, receiverId, requestDateTime, status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        try (Connection connection = DatabaseConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM friend_requests")) {

            while (resultSet.next()) {
                long senderId = resultSet.getLong("sender_id");
                long receiverId = resultSet.getLong("receiver_id");
                LocalDateTime requestDateTime = resultSet.getTimestamp("request_datetime").toLocalDateTime();
                String statusString = resultSet.getString("status");
                FriendRequest.Status status = FriendRequest.Status.valueOf(statusString);

                FriendRequest friendRequest = new FriendRequest(senderId, receiverId, requestDateTime, status);
                friendRequests.add(friendRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO friend_requests (sender_id, receiver_id, request_datetime, status) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setLong(1, entity.getSenderId());
            preparedStatement.setLong(2, entity.getReceiverId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getRequestDateTime()));
            preparedStatement.setString(4, entity.getStatus().name());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> id) {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM friend_requests WHERE sender_id = ? AND receiver_id = ?")) {

            preparedStatement.setLong(1, id.getLeft());
            preparedStatement.setLong(2, id.getRight());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                // Deletion successful
                return findOne(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE friend_requests SET status = ? WHERE sender_id = ? AND receiver_id = ?")) {

            preparedStatement.setString(1, entity.getStatus().name());
            preparedStatement.setLong(2, entity.getSenderId());
            preparedStatement.setLong(3, entity.getReceiverId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }
}
