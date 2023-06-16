package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.utils.ScoreDB;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class ScoreView implements AbstractView {
    @FXML
    public TableColumn<User, String> usernameColumn;
    @FXML
    public TableColumn<User, Integer> pointsColumn;
    @FXML
    public Button exitButton;
    @FXML
    public TableView<User> scoreTable;
    @FXML
    void initialize() {
        List<User> users = ScoreDB.getInstance().getScores().stream().map(it -> new User(it.username(), it.score())).toList();
        scoreTable.setItems(FXCollections.observableArrayList(users));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        exitButton.setOnAction(event -> MainView.setView(ViewStage.MENU));
    }

    public static class User {
        private final SimpleStringProperty username;
        private final SimpleIntegerProperty score;

        User(String username, Integer score){
            this.username = new SimpleStringProperty(username);
            this.score = new SimpleIntegerProperty(score);
        }
        public String getUsername(){ return username.get();}
        public Integer getScore(){ return score.get();}
    }
}
