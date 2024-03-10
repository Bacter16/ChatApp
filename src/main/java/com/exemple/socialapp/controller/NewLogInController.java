package com.exemple.socialapp.controller;

import com.exemple.socialapp.SocialApp;
import com.exemple.socialapp.domain.User;
import com.exemple.socialapp.repository.UserDBRepository;
import com.exemple.socialapp.service.ServiceUsers;
import com.exemple.socialapp.util.PasswordHasher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class NewLogInController {

    @FXML
    private AnchorPane sign_in_anchorpane;

    @FXML
    private AnchorPane sign_in_pane;

    @FXML
    private TextField sign_in_show_password_field;

    @FXML
    private TextField sign_up_show_password_field;

    @FXML
    private TextField sign_up_show_confirm_password_field;

    @FXML
    private PasswordField sign_up_confirm_password;

    @FXML
    private ImageView sign_in_image;

    @FXML
    private Label sign_in_label;

    @FXML
    private TextField sign_in_username;

    @FXML
    private CheckBox sign_in_show_password;

    @FXML
    private Hyperlink sign_in_forgot_password;

    @FXML
    private Button sign_in_button;

    @FXML
    private Label create_account_label;

    @FXML
    private PasswordField sign_in_password;

    @FXML
    private AnchorPane sign_up_pane;

    @FXML
    private ImageView sign_up_image;

    @FXML
    private Label sign_up_label;

    @FXML
    private TextField sign_up_first_name;

    @FXML
    private CheckBox sign_up_show_password;

    @FXML
    private Button sign_up_button;

    @FXML
    private Label sign_up_sign_in_label;

    @FXML
    private PasswordField sign_up_password;

    @FXML
    private TextField sign_up_last_name;

    @FXML
    private TextField sign_up_username;

    @FXML
    private PasswordField sign_in_confirm_password;

    private final ServiceUsers<Long, User> userService;

    private static User loggedInUser;

    public NewLogInController() {
        this.userService = new ServiceUsers<>(new UserDBRepository());
    }

    @FXML
    private void handleLogin() {
        String username = sign_in_username.getText();
        String password;
        if(sign_in_password.isVisible())
            password = sign_in_password.getText();
        else
            password = sign_in_show_password_field.getText();

        var list = isValidLogin(username, password);

        boolean found = (boolean) list.get("found");
        boolean isAdmin = (boolean) list.get("isAdmin");

        if( found )
            openAppropriateView(isAdmin);
        else
            showWorning("Invalid username or password. Please try again.");

        clearFields();

    }

    @FXML
    private void handleSignUp(){
        String first_name = sign_up_first_name.getText();
        String last_name = sign_up_last_name.getText();
        String username = sign_up_username.getText();
        if(userService.findUsersByUsername(username)) {
            showWorning("Username already exists!");
            return;
        }
        String password;
        if(sign_up_password.isVisible())
            password = sign_up_password.getText();
        else
            password = sign_up_show_password_field.getText();

        String password_confirm;
        if(sign_up_confirm_password.isVisible())
            password_confirm = sign_up_confirm_password.getText();
        else
            password_confirm = sign_up_show_confirm_password_field.getText();
        if(!Objects.equals(password_confirm, password)) {
            showWorning("Passwords are not the same!");
            return;
        }
        String hashedPassword = PasswordHasher.hashPassword(password);

        User user = new User(first_name, last_name, username, hashedPassword);
        userService.add(user);

        showAlert("Account created successfully!");
        sign_in_pane.setVisible(true);
        sign_up_pane.setVisible(false);
        clearFields();

    }

    private void clearFields() {
        sign_in_username.clear();
        sign_in_password.clear();
        sign_in_show_password_field.clear();
        sign_up_password.clear();
        sign_up_show_password_field.clear();
        sign_up_confirm_password.clear();
        sign_up_show_confirm_password_field.clear();
        sign_up_first_name.clear();
        sign_up_last_name.clear();
        sign_in_username.clear();
        sign_up_username.clear();

    }

    private Map isValidLogin(String username, String password) {
        Iterable<User> users = userService.findAll();
        List<User> users_list = new ArrayList<>();
        users.forEach(users_list::add);

        boolean isAdmin = false;
        boolean found = false;
        for(User user: users_list){
            if(Objects.equals(user.getUsername(), username) && Objects.equals(user.getPassword(), PasswordHasher.hashPassword(password)) && user.isAdmin()){
                loggedInUser = user;
                found = true;
                isAdmin = true;
                break;
            } else if(Objects.equals(user.getUsername(), username) && Objects.equals(user.getPassword(), PasswordHasher.hashPassword(password))){
                loggedInUser = user;
                found = true;
                break;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("found", found);
        result.put("isAdmin", isAdmin);

        return result;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWorning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openAppropriateView(boolean isAdmin) {
        try {
            FXMLLoader fxmlLoader;

            if (isAdmin) {
                fxmlLoader = new FXMLLoader(SocialApp.class.getResource("admin-view.fxml"));
                closeLoginWindow();
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                Image icon = new Image("C:\\Users\\Bacter\\Documents\\GitHub\\Teme_MAP\\SocialNetwork\\src\\main\\resources\\images\\facebook.jpg");
                stage.getIcons().add(icon);

                stage.setScene(scene);
                stage.show();
            } else {
                fxmlLoader = new FXMLLoader(SocialApp.class.getResource("chat-window.fxml"));
                Parent root = fxmlLoader.load();
                MainWindowController userProfileController = fxmlLoader.getController();
                userProfileController.setLoggedInUser(loggedInUser);
                closeLoginWindow();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                Image icon = new Image("C:\\Users\\Bacter\\Documents\\GitHub\\Teme_MAP\\SocialNetwork\\src\\main\\resources\\images\\facebook.jpg");
                stage.getIcons().add(icon);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeLoginWindow() {
        Stage stage = (Stage) sign_in_button.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void switchToSignUp() {
        sign_in_pane.setVisible(false);
        sign_up_pane.setVisible(true);
    }

    @FXML
    private void switchToSignIn() {
        sign_up_pane.setVisible(false);
        sign_in_pane.setVisible(true);
    }

    @FXML
    private void show_password(){
        if(sign_in_show_password.isSelected()){
            sign_in_show_password_field.setText(sign_in_password.getText());
            sign_in_show_password_field.setVisible(true);
            sign_in_password.setVisible(false);
        }else {
            sign_in_password.setText(sign_in_show_password_field.getText());
            sign_in_password.setVisible(true);
            sign_in_show_password_field.setVisible(false);
        }
    }

    @FXML
    private void show_password_sign_up(){
        if(sign_up_show_password.isSelected()){
            sign_up_show_password_field.setText(sign_up_password.getText());
            sign_up_show_confirm_password_field.setText(sign_up_confirm_password.getText());
            sign_up_show_password_field.setVisible(true);
            sign_up_show_confirm_password_field.setVisible(true);
            sign_up_password.setVisible(false);
            sign_up_confirm_password.setVisible(false);
        }else {
            sign_up_password.setText(sign_up_show_password_field.getText());
            sign_up_confirm_password.setText(sign_up_show_confirm_password_field.getText());
            sign_up_password.setVisible(true);
            sign_up_confirm_password.setVisible(true);
            sign_up_show_password_field.setVisible(false);
            sign_up_show_confirm_password_field.setVisible(false);
        }
    }


}
