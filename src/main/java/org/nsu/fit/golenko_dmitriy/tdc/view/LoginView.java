package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginView {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField loginField;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private TextField passwordField;

    @FXML
    void initialize() {
        loginBtn.setOnAction(event -> {
            Parent menu;
            MainView.presenter.authorization(loginField.getText(), passwordField.getText());
            FXMLLoader fxmlMenu = new FXMLLoader(Objects.requireNonNull(MainView.class.getResource("/Menu.fxml")));
            try {
                menu = fxmlMenu.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MainMenuView mainMenuView = fxmlMenu.getController();
            mainMenuView.usernameLabel.setText(loginField.getText());
            MainView.setScene(menu);
        });
        backBtn.setOnAction(event -> {
            try {
                MainView.setScene(FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("/AuthView.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        exitBtn.setOnAction(event -> System.exit(0));
    }

}
