package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import lombok.Getter;
import lombok.Setter;
import org.nsu.fit.golenko_dmitriy.tdc.model.*;
import org.nsu.fit.golenko_dmitriy.tdc.utils.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

import java.util.List;
import java.util.Map.Entry;

// CR: maybe split into two presenters
public class Presenter implements UpdateListener, GameEndListener {

    @Getter
    private UserData userData;
    @Setter
    private UpdateListener updateListener;
    @Setter
    private Game game;

    public Presenter() {
    }

    public void authorization(String login, String password) {
        if (PlayersDB.getInstance().auth(login, password) == null) {
            MainView.showAlert("Incorrect login", "This user does not exist");
            return;
        }
        authorizedSuccessfully(login);
    }

    public void registration(String login, String password) {
        if (!PlayersDB.getInstance().addUser(login, password)) {
            MainView.showAlert("Error", "User is already exist");
            return;
        }
        authorizedSuccessfully(login);
    }

    public void authorizedSuccessfully(String username) {
        userData = new UserData(username);
        MainView.setView(ViewStage.MENU);
    }

    public List<Entry<String, Integer>> getScore() {
        return ScoreDB.getInstance().getAllScore().entrySet().stream().toList();
    }

    public void start() {
        game.start();
    }

    public int getRoadLen() {
        return game.getRoadLen();
    }

    @Override
    public void update(GameDTO data) {
        updateListener.update(data);
    }

    public void createTower(int cell) {
        game.createTower(cell);
    }

    @Override
    public void end(int score) {
        if (game.isLoop()) {
            game.end();
        }
        if (ScoreDB.getInstance().updateScore(userData.username()) < score) {
            ScoreDB.getInstance().changeUserScore(userData.username(), score);
        }
    }
}
