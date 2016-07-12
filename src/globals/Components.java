package globals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.w3c.dom.Attr;
import sample.Controller;
import tables.Attribute;
import tables.Table;

import java.util.ArrayList;

public class Components {
    static Stage tempStage;

    public static void simpleInfoBox (String title, String message, String button) {
        Stage window = buildWindow(title);
        Label l = new Label(message);
        l.setWrapText(true);

        Button b = new Button(button);

        b.setOnAction(e -> window.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(l, b);
        layout.setAlignment(Pos.CENTER);

        int width;
        int x = 6;
        int y = 150;
        width = 250;

        layout.setOnKeyReleased(e ->
        {
            if (e.getCode() == KeyCode.ESCAPE)
                window.close();
        });

        window.setScene(new Scene(layout, width, 150));
        window.show();
    }

    public static void simpleInfoBox (String title, String message)
    {
        simpleInfoBox(title, message, "OK");
    }

    public static ForeignKeyReference foreignKeyInfoBox (ArrayList<Table> allTables, Table curTable) {

        ForeignKeyReference refInfo = new ForeignKeyReference();

        Stage window = buildWindowNoClose("Foreign key Settings");
        Label l = new Label("Select the table you want to reference to:");
        l.setWrapText(true);

        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText("Select Table");

        ComboBox<String> cb1 = new ComboBox<>();
        cb1.setPromptText("Select Attribute");

        ObservableList<String> values = FXCollections.observableArrayList();
        for (Table t : allTables)  {
            if (t.hasPrimary() && t != curTable) {
                values.add(t.getName());
            }
        }
        cb.setItems(values);

        cb.setOnAction(e -> {
            ObservableList<String> valuesAttributes = FXCollections.observableArrayList();
            for (Table t : allTables) {
                if (t.getName().equals(cb.getValue())) {
                    for (Attribute a : t.getAttributes()) {
                        if (a.isPrimaryKey()) {
                            valuesAttributes.add(a.getName());
                        }
                    }
                }
            }

            cb1.setItems(valuesAttributes);
        });

        Label l1 = new Label("Select the attribute you want to reference to:");
        l1.setWrapText(true);

        Button b = new Button("Done");

        b.setOnAction(e -> {
            if (cb.getValue() != null && cb1.getValue() != null) {
                for (Table table : allTables) {
                    if (table.getName().equals(cb.getValue())) {
                        refInfo.setReferencedTable(table);

                        for (Attribute attribute : table.getAttributes()) {
                            if (attribute.getName().equals(cb1.getValue())) {
                                refInfo.setReferencedAttribute(attribute);
                            }
                        }
                    }
                }
                window.close();

            } else {
                simpleInfoBox("Warning", "Please select your foreign key reference infos!");
            }
        });

        VBox layout = new VBox(15);
        layout.getChildren().addAll(l, cb, l1, cb1, b);
        layout.setAlignment(Pos.CENTER);

        window.setScene(new Scene(layout, 400, 300));
        window.showAndWait();

        return refInfo;
    }

    private static Stage buildWindow (String title) {
        tempStage = new Stage();
        tempStage.initStyle(StageStyle.UTILITY);
        tempStage.setResizable(false);
        tempStage.initModality(Modality.APPLICATION_MODAL);
        tempStage.setTitle(title);
        return tempStage;
    }

    private static Stage buildWindowNoClose (String title) {
        tempStage = new Stage();
        tempStage.initStyle(StageStyle.UTILITY);
        tempStage.setResizable(false);
        tempStage.initModality(Modality.APPLICATION_MODAL);
        tempStage.setTitle(title);
        tempStage.setOnCloseRequest(e -> {e.consume();});
        return tempStage;
    }

    static boolean okay = false;
    static boolean tempOkay = false;

    public static boolean ok (String title, String message) {
        Stage window = buildWindow(title);
        Label l = new Label(message);

        Button bo = new Button("_OK");
        Button ba = new Button("_Abbrechen");

        bo.setOnAction(e ->
        {
            tempOkay = true;
            window.close();
        });

        ba.setOnAction(e ->
        {
            tempOkay = false;
            window.close();
        });

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20));
        buttons.getChildren().addAll(bo, ba);

        VBox layout = new VBox(20);
        layout.getChildren().addAll(l, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40, 20, 20, 20));

        int width;
        int x = 6;
        int y = 150;

        width = message.length() * x + y;

        layout.setOnKeyReleased(e ->
        {
            if (e.getCode() == KeyCode.ESCAPE)
                window.close();
        });

        window.setScene(new Scene(layout, width, 150));
        window.showAndWait();

        okay = tempOkay;
        tempOkay = false;

        return okay;
    }
}
