package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import lombok.Getter;
import lombok.Setter;
import org.nsu.fit.golenko_dmitriy.tdc.exception.AuthException;
import org.nsu.fit.golenko_dmitriy.tdc.model.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.model.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.model.FiledData;
import org.nsu.fit.golenko_dmitriy.tdc.model.Game;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

public class Presenter implements AuthListener, UpdateListener,
        GameStartListener,
        GameEndListener {

    @Getter
    private UserData userData;
    @Setter
    private UpdateListener updateListener;
    @Setter
    private Game game;
    private final PlayersDB database;

    public Presenter(){
        database = new PlayersDB();
    }


    public void authorization(String login, String password){
        try {
            if (login.isEmpty() || password.isEmpty()){
                throw new AuthException("Incorrect login or password field");
            }
            if (!database.auth(login, password)){
                MainView.setView(ViewStage.LOGIN);
                throw new AuthException("Undefined login");
            }
        } catch (AuthException e){
            MainView.showAlert(e.getClass().toString(), e.getMessage());
            return;
        }
        authorizedSuccessfully(login);
    }

    public void registration(String login, String password){
        try {
            if (login.isEmpty() || password.isEmpty()){
                throw new AuthException("Incorrect login or password field");
            }
            if (database.auth(login,password)){
                throw new AuthException("User is already exist");
            }
        } catch (AuthException e){
            MainView.showAlert(e.getClass().toString(),e.getMessage());
            return;
        }
        database.addUser(login,password);
        authorizedSuccessfully(login);
    }

    @Override
    public void authorizedSuccessfully(String username) {
        //TODO SCORE DB
        userData = new UserData(username,0);
        MainView.setView(ViewStage.MENU, userData);
    }

//    @Override
//    public void exceptionHandling(String context, @NotNull Throwable e) {
//        Platform.runLater(() -> MainView.showAlert(
//                "Session forced to crash!",
//                "Message: " + context + ", throwable=" + e
//        ));
//    }

    @Override
    public void start() {
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
