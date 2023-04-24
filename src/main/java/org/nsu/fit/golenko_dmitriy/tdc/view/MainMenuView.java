package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.nsu.fit.golenko_dmitriy.tdc.Main;

public class MainMenuView {

    @FXML
    public Label usernameLabel;
    @FXML
    public Button logoutBtn;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button scoreBtn;

    @FXML
    private Button createNewAccountBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Button joinBtn;

    @FXML
    private TextField loginField;

    @FXML
    private Button newGameBtn;

    @FXML
    private TextField passwordField;

    @FXML
    private Button signUpBtn;

    public void changeLabel(String username) {
        usernameLabel.setText(username);
    }

    @FXML
    void initialize() {
        logoutBtn.setOnAction(event -> {
            FXMLLoader fxmlAuth = new FXMLLoader(Objects.requireNonNull(MainView.class.getResource("/AuthView.fxml")));
            try {
                MainView.setScene(fxmlAuth.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        exitBtn.setOnAction(event -> System.exit(0));
        scoreBtn.setOnAction(event -> {
            System.out.println(11311);
        });
    }

}
