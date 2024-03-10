package com.exemple.socialapp.repository;

import com.exemple.socialapp.domain.User;
import com.exemple.socialapp.repository.paging.*;
import com.exemple.socialapp.util.DatabaseConnector;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements PagingRepository<Long, User> {

//    private Validator<User> validator;

    public UserDBRepository() {}



    @Override
    public Optional<User> findOne(Long longID) {
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?")

        ) {
            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("hashedpassword");
                boolean isAdmin=resultSet.getBoolean("admin");
                List<Long> friends = new ArrayList<>();
                PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM friendship\n" +
                        "WHERE id1 = ? OR id2 = ?");
                statement1.setLong(1, longID);
                statement1.setLong(2, longID);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                    Long user_id1= resultSet1.getLong("id1");
                    Long user_id2= resultSet1.getLong("id2");
                    if(user_id1.equals(longID)){
                        friends.add(user_id2);
                    }else{
                        friends.add(user_id1);
                    }
                }
                User u=new User(firstName, lastName, username, password, friends, isAdmin);
                u.setId(longID);
                return Optional.of(u);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from users")
        ) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("hashedpassword");
                boolean isAdmin=resultSet.getBoolean("admin");
                List<Long> friends = new ArrayList<>();
                PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM friendship\n" +
                        "WHERE id1 = ? OR id2 = ?");
                statement1.setLong(1, id);
                statement1.setLong(2, id);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                    Long user_id1= resultSet1.getLong("id1");
                    Long user_id2= resultSet1.getLong("id2");
                    if(user_id1.equals(id)){
                        friends.add(user_id2);
                    }else{
                        friends.add(user_id1);
                    }
                }
                User user=new User(firstName,lastName,username,password, friends, isAdmin);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        String insertSQL="insert into users (first_name, last_name, username, hashedpassword) values(?,?,?,?)";
        try (var connection = DatabaseConnector.getConnection();
             PreparedStatement statement=connection.prepareStatement(insertSQL))
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getUsername());
            statement.setString(4,entity.getPassword());
            int response=statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> delete(Long aLong) {
        if (aLong == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        String deleteSQL="delete from users where id=?";
        try (var connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSQL)
             ) {
            statement.setLong(1, aLong);

            Optional<User> foundUser = findOne(aLong);

            int response = 0;
            if (foundUser.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        if(entity==null)
        {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL="update users set first_name=?,last_name=? where id=?";
        try(var connection= DatabaseConnector.getConnection();
            PreparedStatement statement=connection.prepareStatement(updateSQL))
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setLong(3,entity.getId());

            int response= statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Page<User> find_all(Pageable pageable) {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users LIMIT ? OFFSET ?")) {

            int pageSize = pageable.pageSize();
            int pageNumber = pageable.pageNumber();

            statement.setInt(1, pageSize);
            statement.setInt(2, pageNumber * pageSize);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("hashedpassword");
                boolean isAdmin = resultSet.getBoolean("admin");
                List<Long> friends = new ArrayList<>();

                User user = new User(firstName, lastName, username, password, friends, isAdmin);
                user.setId(id);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new PageImplementation<>(new PageableImplementation(pageable.pageNumber(), pageable.pageSize()), users.stream());
    }
}
