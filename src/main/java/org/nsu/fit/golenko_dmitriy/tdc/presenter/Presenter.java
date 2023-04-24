package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import org.nsu.fit.golenko_dmitriy.tdc.model.TDServerHandler;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;

public class Presenter {
    TDServerHandler tdServerHandler;
    MainView mainView;

    public Presenter(MainView mainView, TDServerHandler tdServerHandler) {
        this.tdServerHandler = tdServerHandler;
        this.mainView = mainView;
        mainView.setPresenter(this);
    }

    public void authorization(String login, String password){
        tdServerHandler.authorization(login, password);
    }

    public void registration(String login, String password){
        tdServerHandler.registration(login, password);
    }

}
