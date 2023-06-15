package org.nsu.fit.golenko_dmitriy.tdc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class LoginView implements AbstractView {

    @FXML
    private Button backBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    void initialize() {
        loginBtn.setOnAction(
                event -> MainView.getPresenter().authorization(loginField.getText(), passwordField.getText()));
        backBtn.setOnAction(event -> MainView.setView(ViewStage.AUTH));
        exitBtn.setOnAction(event -> System.exit(0));
    }

}
