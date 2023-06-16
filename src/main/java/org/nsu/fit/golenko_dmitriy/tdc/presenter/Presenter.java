package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import lombok.Getter;
import lombok.Setter;
import org.nsu.fit.golenko_dmitriy.tdc.model.*;
import org.nsu.fit.golenko_dmitriy.tdc.utils.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;


// CR: maybe split into two presenters
public class Presenter implements ActionListener {

    @Getter
    private UserData userData;
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
        if (!PlayersDB.getInstance().addUser(login, password)) {
            MainView.showAlert("Error", "User is already exist");
            return;
        }
        authorizedSuccessfully(login);
    }

    public void authorizedSuccessfully(String username) {
        userData = new UserData(username, ScoreDB.getInstance().getUserScore(username));
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
    public void end() {
        actionListener.end();
        if (game.isLoop()) {
            game.end();
        }
        ScoreDB.getInstance().changeUserScore(userData.getUsername(), userData.getScore());
    }
}
