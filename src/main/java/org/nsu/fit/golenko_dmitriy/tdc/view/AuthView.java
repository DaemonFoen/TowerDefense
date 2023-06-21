package org.nsu.fit.golenko_dmitriy.tdc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.nsu.fit.golenko_dmitriy.tdc.utils.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class AuthView implements AbstractView {
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
        exitBtn.setOnAction(event -> {
            PlayersDB.getInstance().flush();
            ScoreDB.getInstance().flush();
            System.exit(0);
        });
    }
}