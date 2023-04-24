package org.nsu.fit.golenko_dmitriy.tdc.view;

import java.io.FileInputStream;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;

public class MainView{
    static Stage stage;
    static Presenter presenter;

    public void setPresenter(Presenter presenter){
        MainView.presenter = presenter;
    }

    public MainView(Stage stage) {
        MainView.stage = stage;
        try {
            Image image = new Image(new FileInputStream("src/main/resources/21312313.PNG"));
            stage.getIcons().add(image);
            stage.setTitle("Tower Defence");
            setScene(FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("/AuthView.fxml"))));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setScene(Parent root) {
        Scene scene = new Scene(root, 900, 850);
        stage.setScene(scene);
        stage.show();
    }
}
