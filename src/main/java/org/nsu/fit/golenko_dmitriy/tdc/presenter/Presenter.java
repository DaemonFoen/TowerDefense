package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import javafx.application.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.nsu.fit.golenko_dmitriy.tdc.exception.AuthException;
import org.nsu.fit.golenko_dmitriy.tdc.exception.RegistrationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.model.FiledData;
import org.nsu.fit.golenko_dmitriy.tdc.model.Game;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
@NoArgsConstructor
public class Presenter implements AuthListener, ExceptionListener, UpdateListener,
        GameStartListener,
        GameEndListener {

    @Getter
    private UserData userData;
    @Setter
    private UpdateListener updateListener;
    private Game game;

    public void authorization(String login, String password){
        try {
            //TODO логин в бд
        } catch (AuthException e){
            MainView.showAlert(e.getClass().toString(), e.getMessage());
        }
        authorizedSuccessfully(login);
    }

    public void registration(String login, String password){
        try {
            //TODO как и в авторизации
        } catch (RegistrationException e){
            MainView.showAlert(e.getClass().toString(),e.getMessage());
        }
        authorizedSuccessfully(login);
    }

    @Override
    public void authorizedSuccessfully(String username) {
        userData = new UserData(username,0);
        MainView.setView(ViewStage.MENU, userData);
    }

    @Override
    public void exceptionHandling(String context, @NotNull Throwable e) {
        Platform.runLater(() -> MainView.showAlert(
                "Session forced to crash!",
                "Message: " + context + ", throwable=" + e
        ));
    }

    @Override
    public void start() {
        game = new Game(this,this);
        game.start();
    }

    @Override
    public void update(FiledData data) {
        updateListener.update(data);
    }

    public void createTower(int id){
        game.createTower(id);
    }

    @Override
    public void end() {
        //TODO score
    }
}
