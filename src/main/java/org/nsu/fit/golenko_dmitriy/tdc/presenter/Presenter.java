package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.model.*;
import org.nsu.fit.golenko_dmitriy.tdc.utils.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class Presenter implements ActionListener {

    @Getter
    private String username;
    @Setter
    private ActionListener actionListener;
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
        assert login != null;
        assert password != null;
        if (!PlayersDB.getInstance().addUser(login, password)) {
            MainView.showAlert("Error", "User already exists");
            return;
        }
        authorizedSuccessfully(login);
    }

    public void authorizedSuccessfully(String username) {
        assert username != null;
        this.username = username;
        log.info("auth success  login: " + username);
        MainView.setView(ViewStage.MENU);
    }

    public void start() {
        game.start();
    }

    @Override
    public void update(GameDTO data) {
        actionListener.update(data);
    }

    public void createTower(int cell) {
        game.createTower(cell);
    }

    @Override
    public void end(int score) {
        if (game.isLoop()) {
            game.end();
        }
        ScoreDB.getInstance().changeUserScore(username, score);
    }
}
