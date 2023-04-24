package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.nsu.fit.golenko_dmitriy.tdc.Main;

public class AuthView {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane AuthPane;

    @FXML
    private Button exitBtn;

    @FXML
    private Button loginBtn;
    @FXML
    private Button regBtn;

    @FXML
    void initialize() {
        loginBtn.setOnAction(event -> {
            try {
                MainView.setScene(FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("/LoginView.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        regBtn.setOnAction(event -> {
            try {
                MainView.setScene(FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("/RegistrationView.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        exitBtn.setOnAction(event -> System.exit(0));
    }
}

