package org.nsu.fit.golenko_dmitriy.tdc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.nsu.fit.golenko_dmitriy.tdc.exception.AuthException;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class RegistrationView implements AbstractView {

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
    void initialize() {
        regBtn.setOnAction(
                event -> {
                    if (!loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                        MainView.getPresenter().registration(loginField.getText(), passwordField.getText());
                    }
                });
        backBtn.setOnAction(event -> MainView.setView(ViewStage.AUTH));
        exitBtn.setOnAction(event -> System.exit(0));
    }
}
