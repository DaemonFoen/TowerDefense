package org.nsu.fit.golenko_dmitriy.tdc.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class MenuView implements AbstractView {
    private final UserData userData;

    @FXML
    public Label usernameLabel;

    @FXML
    public Button logoutBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Button joinBtn;

    @FXML
    private Button newGameBtn;

    public MenuView(UserData data) {
        this.userData = data;
    }

    @FXML
    void initialize() {

        usernameLabel.setText(userData.getUsername());
        newGameBtn.setOnAction(event -> {
            MainView.setView(ViewStage.LOBBY, userData);
        });
        logoutBtn.setOnAction(event -> {
            userData.getWebClient().close();
            MainView.setView(ViewStage.AUTH);
        });
        exitBtn.setOnAction(event -> System.exit(0));
    }

}
