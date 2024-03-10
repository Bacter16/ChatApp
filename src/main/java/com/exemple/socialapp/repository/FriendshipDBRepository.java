package com.exemple.socialapp.repository;



import com.exemple.socialapp.domain.Friendship;
import com.exemple.socialapp.domain.Tuple;
import com.exemple.socialapp.util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {

    public FriendshipDBRepository(){}

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> longLongTuple) {
        String selectSQL = "select * from friendship where id1 = ? and id2 = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setLong(1, longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long u1 = resultSet.getLong("idfriend1");
                long u2 = resultSet.getLong("idfriend2");
                Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime date = timestamp.toLocalDateTime();
                Friendship friendship = new Friendship(u1, u2, date);
                Tuple<Long, Long> id = new Tuple<>(u1, u2);
                friendship.setId(id);
                return Optional.of(friendship);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        String selectAllSQL = "select * from friendship";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectAllSQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long u1 = resultSet.getLong("idfriend1");
                long u2 = resultSet.getLong("idfriend2");
                Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime date = timestamp.toLocalDateTime();
                Friendship friendship = new Friendship(u1, u2, date);
                Tuple<Long, Long> id = new Tuple<>(u1, u2);
                friendship.setId(id);
                friendships.add(friendship);
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        String insertSQL = "insert into friendship (id1, id2, date) values (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setLong(1, entity.getU1());
            statement.setLong(2, entity.getU2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> longLongTuple) {
        String deleteSQL = "delete from friendship where id1 = ? and id2 = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setLong(1, longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());

            Optional<Friendship> foundFriendship = findOne(longLongTuple);

            int response = 0;
            if (foundFriendship.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : foundFriendship;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL = "update friendship set date = ? where id1 = ? and id2 = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setLong(2, entity.getU1());
            statement.setLong(3, entity.getU2());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
