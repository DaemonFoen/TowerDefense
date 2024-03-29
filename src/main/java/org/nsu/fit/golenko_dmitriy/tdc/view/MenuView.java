package org.nsu.fit.golenko_dmitriy.tdc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.nsu.fit.golenko_dmitriy.tdc.utils.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class MenuView implements AbstractView {

    private final String username;

    @FXML
    public Label usernameLabel;

    @FXML
    public Button logoutButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button scoreButton;

    @FXML
    private Button newGameButton;

    public MenuView(String username) {
        this.username = username;
    }

    @FXML
    void initialize() {
        usernameLabel.setText(username);
        newGameButton.setOnAction(event -> MainView.setView(ViewStage.GAME));
        logoutButton.setOnAction(event -> MainView.setView(ViewStage.AUTH));
        scoreButton.setOnAction(event -> MainView.setView(ViewStage.SCORE));
        exitButton.setOnAction(event -> {
            PlayersDB.getInstance().flush();
            ScoreDB.getInstance().flush();
            System.exit(0);
        });
    }

}
