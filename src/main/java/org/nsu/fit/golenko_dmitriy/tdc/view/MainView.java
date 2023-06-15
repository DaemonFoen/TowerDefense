package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.model.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;

@Log4j2
public class MainView {

    private static Stage stage;
    @Getter
    @Setter
    private static Presenter presenter;

    public static void initView(Stage stage) {
        try {
            Image image = new Image(new FileInputStream("src/main/resources/icon.PNG"));
            stage.getIcons().add(image);
            stage.setTitle("Tower Defence");
        } catch (FileNotFoundException e) {
            log.debug(e.getMessage());
        }
        MainView.stage = stage;
    }

    public static void setView(MainView.ViewStage stage) {
        setView(stage, null);
    }

    public static void setView(MainView.ViewStage stage, UserData userData) {
        try {
            switch (stage) {
                case AUTH -> setScene(
                        load(Objects.requireNonNull(MainView.class.getResource("/auth.fxml")), new AuthView()));
                case REG -> setScene(load(Objects.requireNonNull(MainView.class.getResource("/registration.fxml")),
                        new RegistrationView()));
                case MENU -> setScene(
                        load(Objects.requireNonNull(MainView.class.getResource("/menu.fxml")), new MenuView(userData)));
                case LOGIN -> setScene(
                        load(Objects.requireNonNull(MainView.class.getResource("/login.fxml")), new LoginView()));
                case GAME -> setScene(
                        load(Objects.requireNonNull(MainView.class.getResource("/game.fxml")), new GameView(userData)));
//                case LOBBY -> setScene(load(Objects.requireNonNull(MainView.class.getResource("/lobby.fxml")),
//                        new LobbyView(userData)));
            }
        } catch (Throwable e) {
            log.debug("Stage : " + stage.toString() + "error message : " + e.getMessage());
            e.printStackTrace();
            showAlert(e.getClass().toString(), e.getMessage());
        }
    }

    public static void showAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Tower defense : exception caught");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    private static Parent load(URL fxmlPath, AbstractView view) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlPath);
        loader.setController(view);
        return loader.load();
    }


    private static void setScene(Parent root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public enum ViewStage {AUTH, LOGIN, REG, MENU, LOBBY, GAME}
}
