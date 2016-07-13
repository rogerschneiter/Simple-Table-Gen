package tutorials;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class CreateTable {
    public CreateTable(Scene scene) {
        Tooltip enterTableName = new Tooltip("Enter the name here!");
        Tooltip createTable = new Tooltip("Click here to create it!");

        TextField tf = (TextField) scene.lookup("#tableName");
        tf.setTooltip(enterTableName);
    }
}
