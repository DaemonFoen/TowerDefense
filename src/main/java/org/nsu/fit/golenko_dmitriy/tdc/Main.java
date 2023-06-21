package org.nsu.fit.golenko_dmitriy.tdc;

import javafx.application.Application;
import javafx.stage.Stage;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.model.Game;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;
import org.nsu.fit.golenko_dmitriy.tdc.utils.PlayersDB;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;


public class Main extends Application {
    public static void main(String[] args) {
        Application.launch();
    }
    @Override
    public void start(Stage stage) {

        Configuration.getInstance();
        EntityCreator.create(Type.DEFAULT_TOWER);
        PlayersDB.getInstance();
        System.out.println(ScoreDB.getInstance());

        MainView.initView(stage);
        Presenter presenter = new Presenter();
        MainView.setPresenter(presenter);
        MainView.setView(ViewStage.AUTH);
        presenter.setGame(new Game(Configuration.getInstance(), presenter));
    }
}