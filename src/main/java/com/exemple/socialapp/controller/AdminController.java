package com.exemple.socialapp.controller;


import com.exemple.socialapp.domain.User;
import com.exemple.socialapp.repository.UserDBRepository;
import com.exemple.socialapp.service.ServiceUsers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class AdminController {

    @FXML
    private TextField idField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TableView<User> userTable;

    private final ServiceUsers<Long, User> userService;

    public AdminController() {
        String url="jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";

        this.userService = new ServiceUsers<>(new UserDBRepository());

    }

    @FXML
    public void initialize() {
        findAllUsers();

        userTable.setRowFactory(tableView -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    User selectedUser = row.getItem();
                    fillFields(selectedUser);
                }
            });
            return row;
        });
    }

    private void fillFields(User user) {
        idField.setText(String.valueOf(user.getId()));
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
    }

    @FXML
    private void createUser() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            showErrorDialog("All fields must be filled");
            return;
        }

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        User newUser = new User(firstName, lastName);

        userService.add(newUser);

        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        findAllUsers();
        showMessageDialog("User created successfully: " + newUser);
    }

    @FXML
    private void readUser() {
        if (idField.getText().isEmpty() ) {
            showErrorDialog("All fields must be filled");
            return;
        }

        Long id = Long.parseLong(idField.getText());

        if(userService.findOne(id).isPresent()) {
            idField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            findAllUsers();
            userService.findOne(id).ifPresent(user -> showMessageDialog("User found successfully: " + user));
        }
        else
            showErrorDialog("User not found with ID: " + id);
    }

    @FXML
    private void updateUser() {
        if (idField.getText().isEmpty() || firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            showErrorDialog("All fields must be filled");
            return;
        }

        Long id = Long.parseLong(idField.getText());
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (idField.getText().isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showErrorDialog("All fields must be filled");
            return;
        }

        User updatedUser = new User(firstName, lastName);
        updatedUser.setId(id);

        userService.update(updatedUser);

        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");

        findAllUsers();
        showMessageDialog("User updated: " + updatedUser);
    }

    @FXML
    private void deleteUser() {
        if (idField.getText().isEmpty()) {
            showErrorDialog("All fields must be filled");
            return;
        }

        Long id = Long.parseLong(idField.getText());

        userService.remove(id);

        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");

        findAllUsers();
        showMessageDialog("User deleted successfully!");
    }

    @FXML
    private void findAllUsers() {
        ObservableList<User> model = FXCollections.observableArrayList();
        Iterable<User> users = userService.findAll();
        List<User> usersList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
        model.setAll(usersList);

        userTable.getItems().clear();
        userTable.getColumns().clear();

        TableColumn<User, Long> idColumn = new TableColumn<>("ID");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First Name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last Name");
        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        userTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, usernameColumn, passwordColumn);

        userTable.setItems(model);
    }

    private void showErrorDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showMessageDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
