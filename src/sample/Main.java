package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("\\res\\icon.png"));
        primaryStage.getIcons().addAll(icon);
        primaryStage.setTitle("Simple Table Gen V 0.1");
        Scene s = new Scene(root, 750, 500);
        s.getStylesheets().add(getClass().getResource("\\res\\style.css").toExternalForm());

        primaryStage.setScene(s);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
