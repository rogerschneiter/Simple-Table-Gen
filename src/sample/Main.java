package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.omg.CORBA.Environment;

import java.io.File;
import java.nio.file.FileSystem;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().addAll(icon);
        primaryStage.setTitle("Simple Table Generator V 1.1");
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        Scene s = new Scene(root, 750, 500);
        s.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(s);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
