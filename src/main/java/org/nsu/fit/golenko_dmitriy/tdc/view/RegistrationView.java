package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class RegistrationView {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button regBtn;

    @FXML
    private AnchorPane regPane;

    @FXML
    void initialize() {
        regBtn.setOnAction(event -> {
            Parent auth;
            MainView.presenter.registration(loginField.getText(), passwordField.getText());
            FXMLLoader fxmlAuth = new FXMLLoader(Objects.requireNonNull(MainView.class.getResource("/AuthView.fxml")));
            try {
                auth = fxmlAuth.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AuthView authView = fxmlAuth.getController();
            MainView.setScene(auth);
        });
        //TODO Визуальная реакция на успех/неудачу регистрации
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
