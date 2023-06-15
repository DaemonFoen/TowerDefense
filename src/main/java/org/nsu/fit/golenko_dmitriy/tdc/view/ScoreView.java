package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class ScoreView implements AbstractView {
    @FXML
    public TableColumn<User, String> usernameColumn;
    @FXML
    public TableColumn<User, String> pointsColumn;
    @FXML
    public Button exitButton;
    @FXML
    public TableView<User> scoreTable;
    @FXML
    void initialize() {
        List<User> users = MainView.getPresenter().getScore().stream().map(it -> new User(it.getKey(), it.getValue())).toList();
        scoreTable.setItems(FXCollections.observableArrayList(users));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        exitButton.setOnAction(event -> MainView.setView(ViewStage.MENU));
    }
    public static class User {
        private final SimpleStringProperty name;
        private final SimpleStringProperty age;

        User(String name, String age){
            this.name = new SimpleStringProperty(name);
            this.age = new SimpleStringProperty(age);
        }
        public String getName(){ return name.get();}
        public String getAge(){ return age.get();}
    }
}