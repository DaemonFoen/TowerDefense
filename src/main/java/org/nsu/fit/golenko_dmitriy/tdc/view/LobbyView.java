package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import org.nsu.fit.golenko_dmitriy.tdc.exception.GeneralClientException;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.UserClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.Lobby;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.User;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.OnGameStartCallbackListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class LobbyView implements AbstractView, Initializable {

    private final UserClient userClient;
    private final Object friendsMonitor = new Object();
    private final Object invitationsMonitor = new Object();
    private List<User> friends = new ArrayList<>();

    @FXML
    private Button exitButton;

    @FXML
    private Button startGameButton;

    @FXML
    private Button createLobbyButton;

    @FXML
    private VBox friendsList;

    @FXML
    private Text friendsNumber;

    public LobbyView(UserData data) {
        this.userClient = new UserClient(data.getWebClient());
    }
    private Optional<User> getUserByAdminSessionId(String sessionId) {
        return friends.stream()
                .filter(it -> it.getWebsocketSessionId().equals(sessionId))
                .findAny();
    }

    @FXML
    void closeLobbyButtonOnClick(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startGameButton.setOnAction(event -> {
            MainView.getPresenter().start();
        });
        exitButton.setOnAction(event -> {
            userClient.close();
            MainView.setView(ViewStage.MENU,MainView.getPresenter().getUserData());
        });
        Platform.runLater(() -> {
            updateFriendList();
            updateFriendListOnline();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        });
    }

    public void updateFriendList() {
        friends = userClient.getFriends().stream().map(it -> User.builder().username(it).build()).toList();
        updateFriendListView(friends);
    }

    public void updateFriendListOnline() {
        HashMap<String, User> friendsOnline = new HashMap<>();
        userClient.getFriendsStatuses().forEach(it -> friendsOnline.put(it.getUsername(), it));
        List<User> list = friends.stream().map(it -> {
            User user = friendsOnline.getOrDefault(it.getUsername(), it);
            it.setStatus(user.getStatus());
            it.setWebsocketSessionId(user.getWebsocketSessionId());
            return user;
        }).toList();
        updateFriendListView(list);
        friendsNumber.setText(Long.toString(friendsOnline.size()));
    }

    private Node getFriendView(User user) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource( "/friend.fxml"));
        loader.setController(new FriendView(user,userClient));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new GeneralClientException(e);
        }
    }

    private void updateFriendListView(List<User> friends) {
        friendsList.getChildren().clear();
        List<Node> list = friends.stream().map(this::getFriendView).toList();
        friendsList.getChildren().addAll(list);
    }
}

