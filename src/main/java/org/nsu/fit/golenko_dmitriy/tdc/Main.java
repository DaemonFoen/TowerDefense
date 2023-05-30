package org.nsu.fit.golenko_dmitriy.tdc;

import javafx.application.Application;
import javafx.stage.Stage;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;


public class Main extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        MainView.initView(stage);
        Presenter presenter = new Presenter();
        MainView.setPresenter(presenter);
        MainView.setView(ViewStage.AUTH);
    }
}