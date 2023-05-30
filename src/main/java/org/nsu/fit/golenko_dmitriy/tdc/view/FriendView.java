package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.UserClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.User;

public class FriendView implements AbstractView, Initializable {

    private final User user;
    private final UserClient client;
    @FXML
    private Circle indicatorCircle;
    @FXML
    private Button inviteButton;
    @FXML
    private Text status;
    @FXML
    private Text username;

    public FriendView(User user, UserClient client) {
        this.user = user;
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inviteButton.setOnAction(event -> client.sendLobbyInvitation(user));
        username.setText(user.getUsername());
        status.setText(user.getStatus().name());
        if (user.getStatus().equals(User.Status.OFFLINE)) {
            indicatorCircle.setStroke(Color.rgb(56, 56, 56));
            indicatorCircle.setFill(Color.rgb(42, 42, 42));
        }
        inviteButton.setVisible(client.getLobby() != null);
        inviteButton.managedProperty().bind(inviteButton.visibleProperty());
        inviteButton.setDisable(!user.getStatus().equals(User.Status.IN_MENU));
    }

}
