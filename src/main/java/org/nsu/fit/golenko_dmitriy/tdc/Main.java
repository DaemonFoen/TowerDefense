package org.nsu.fit.golenko_dmitriy.tdc;

import javafx.application.Application;
import javafx.stage.Stage;
import org.nsu.fit.golenko_dmitriy.tdc.model.TDServerHandler;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView;


public class Main extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        TDServerHandler tdServerHandler = new TDServerHandler();
        MainView mainView = new MainView(stage);
        Presenter presenter = new Presenter(mainView,tdServerHandler);

    }
}



//        try {
//            AnchorPane root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/Menu.fxml")));
//            Image image = new Image(new FileInputStream("src/main/resources/21312313.PNG"));
//            ImageView imageView = new ImageView(image);
//            imageView.setLayoutX(1);
//            imageView.setLayoutY(1);
//            stage.getIcons().add(image);
//            Scene scene = new Scene(root, 900, 850);
//            Button logout = (Button) root.lookup("#logoutBtn");
//            logout.setOnAction(event -> {
//                try {
//                    AnchorPane logRoot = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/AuthView.fxml")));
//                    stage.setScene(new Scene(logRoot, 900, 850));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//            System.out.println(logout);
//            stage.setTitle("Tower Defence");
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }