package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.UserClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.WebClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.ExceptionContext;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Presenter implements OnAuthCallbackListener, OnGameStartCallbackListener, OnExceptionListener,
        OnDisconnectedListener {

    WebClient webClient;

    @Getter
    UserData userData;


    public void authorization(String login, String password){
        webClient = WebClient.authentication(login, password, this, this);
        authorizedSuccessfully(webClient, login);
    }

    public void registration(String login, String password){
        webClient = WebClient.registration(login, password, this, this);
        authorizedSuccessfully(webClient, login);
    }

    @Override
    public void authorizedSuccessfully(WebClient client, String username) {
        this.userData = new UserData(client, username, null);
        MainView.setView(ViewStage.MENU, userData);
    }

    @Override
    public void onDisconnect(@NotNull ExceptionContext data, @NotNull Throwable e) {
        log.debug("onDisconnect() : " + data + ", e.getMessage()=" + e.getMessage());
        Platform.runLater(() -> MainView.showAlert(
                "Session forced to crash!",
                "Message: " + data + ", throwable=" + e
        ));
    }

    @Override
    public void onException(@NotNull ExceptionContext data, @NotNull Throwable e) {
        onDisconnect(data, e);
    }

    @Override
    public void start() {
        UserClient client = new UserClient(userData.getWebClient());
        client.createLobby();
        userData.setUserClient(client);
        MainView.setView(ViewStage.GAME, userData);
    }

}
