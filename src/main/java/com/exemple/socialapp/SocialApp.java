package com.exemple.socialapp;
import com.exemple.socialapp.util.DatabaseConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SocialApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SocialApp.class.getResource("new-log-in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SocialNetwork");
        stage.setScene(scene);
        stage.show();
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConnector::closeDataSource));
    }

    public static void main(String[] args) {
        launch();
    }
}