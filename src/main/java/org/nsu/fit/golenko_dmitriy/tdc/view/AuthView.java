package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class AuthView implements AbstractView {
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
        loginBtn.setOnAction(event -> MainView.setView(ViewStage.LOGIN));
        regBtn.setOnAction(event -> MainView.setView(ViewStage.REG));
        exitBtn.setOnAction(event -> System.exit(0));
    }
}

