package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import lombok.Getter;
import lombok.Setter;
import org.nsu.fit.golenko_dmitriy.tdc.exception.AuthException;
import org.nsu.fit.golenko_dmitriy.tdc.model.*;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

import java.util.List;
import java.util.Map.Entry;

// CR: maybe split into two presenters
public class Presenter implements UpdateListener, GameEndListener {
    private final PlayersDB playersDatabase;
    @Getter
    private final ScoreDB scoreDatabase;
    @Getter
    private UserData userData;
    @Setter
    private UpdateListener updateListener;
    @Setter
    private Game game;

    public Presenter() {
        playersDatabase = new PlayersDB();
        scoreDatabase = new ScoreDB();
    }

    public void authorization(String login, String password) {
        try {
            // CR: check in view
            if (login.isEmpty() || password.isEmpty()) {
                throw new AuthException("Incorrect login or password field");
            }
            if (playersDatabase.auth(login, password) == null) {
                MainView.setView(ViewStage.LOGIN);
                throw new AuthException("Undefined login");
            }
        } catch (AuthException e) {
            // CR: ???
            MainView.showAlert(e.getClass().toString(), e.getMessage());
            return;
        }
        authorizedSuccessfully(login);
    }

    public void registration(String login, String password) {
        try {
            if (login.isEmpty() || password.isEmpty()) {
                throw new AuthException("Incorrect login or password field");
            }
            if (playersDatabase.userExist(login)) {
                throw new AuthException("User is already exist");
            }
        } catch (AuthException e) {
            MainView.showAlert(e.getClass().toString(), e.getMessage());
            return;
        }
        // CR: return boolean
        playersDatabase.addUser(login, password);
        authorizedSuccessfully(login);
    }

    public void authorizedSuccessfully(String username) {
        userData = new UserData(username);
        MainView.setView(ViewStage.MENU);
    }

    public List<Entry<String, String>> getScore() {
        return scoreDatabase.allScore().entrySet().stream().toList();
    }

    public void start() {
        game.start();
    }

    public int getRoadLen() {
        return game.getRoadLen();
    }

    @Override
    public void update(GameData data) {
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
        if (Integer.parseInt(scoreDatabase.updateScore(userData.username())) < score) {
            scoreDatabase.addUser(userData.username(), String.valueOf(score));
        }
    }
}
