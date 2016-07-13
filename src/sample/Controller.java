package sample;

import com.sun.istack.internal.Nullable;
import globals.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.w3c.dom.Attr;
import tables.Attribute;
import tables.Datatype;
import tables.Exports;
import tables.Table;

import java.net.URL;
import java.util.*;

import static globals.Components.foreignKeyInfoBox;
import static globals.Components.ok;

public class Controller implements Initializable {

    // Declaration of Variables
    public TextField tableName;
    public ComboBox selectTable;
    public TextField attributeName;
    public ComboBox datatype;
    public ComboBox selectTableDelete;
    public ComboBox selectAttribute;
    public ComboBox tableToExport;
    public ComboBox wayOfExport;
    public CheckBox isPrimary;
    public CheckBox isForeign;
    public VBox tablePane;
    public VBox dataPane;
    public AnchorPane tabExport;
    public AnchorPane tabAlter;
    public AnchorPane tabDel;
    public AnchorPane tabInsert;
    public Button deleteAttribute;
    public Button newInsert;
    public Button createTable;
    private ArrayList<Table> allTables = new ArrayList<>();

    public void createTable(ActionEvent actionEvent) {
        // If Text is not empty, then:
        if (tableName.getText() != null && !tableName.getText().equals("")) {
            // Create new Instance Table if not exists

            for (Table t : allTables) {
                if (t.getName().equals(tableName.getText())) {
                    Components.simpleInfoBox("Info", "Table already exists!");
                    tableName.setText("");
                    return;
                }
            }

            Table t = new Table(tableName.getText());

            // Add Table Instance to ArrayList allTables
            allTables.add(t);

            // Clear Textfield
            tableName.setText("");

            // Logging: Table generated
            Logger.log(LogTags.INFO, "Controller.createTable(): Table '" + t.getName() + "' created!");

            // Show table in tablePane
            showTable(t);
            selectTable.setValue(t.getName());
            Logger.log(LogTags.INFO, "Controller.createTable(): Table '" + t.getName() + "' showed!");
        } else {
            Components.simpleInfoBox("Info", "Tablename cannot be empty!");
        }
        checkTabStatus();
        refresh();
    }

    public void refresh() {
        ObservableList<String> values = FXCollections.observableArrayList();
        values.clear();

        // Add values to OberservableList values
        for (Table t : allTables) {
            values.add(t.getName());
        }

        // Set Value to ComboBox selectTable and selectTableDelete
        selectTable.setItems(values);
        selectTableDelete.setItems(values);
        tableToExport.setItems(values);

        // Add values to second Obs List
        ObservableList<String> values2 = FXCollections.observableArrayList();
        values2.clear();

        if (selectedTable() != null) {

            for (Attribute a : selectedTable().getAttributes()) {
                values2.add(a.getName());
            }

            selectAttribute.setItems(values2);
        }
    }

    private void showTable(Table t) {
        if (t != null) {
            // clear tablePane
            dataPane.getChildren().clear();

            // Create new TableView
            TableView<String> table = new TableView<String>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // For each Attribute add one Column with correct name
            for (Attribute a : t.getAttributes()) {
                javafx.scene.control.TableColumn<String, String> col1 = new javafx.scene.control.TableColumn<String, String>(a.getName());
                col1.setPrefWidth(a.getName().length() * 15);
                table.getColumns().add(col1);
            }

            // Add table to tablePane
            dataPane.getChildren().add(table);
            refresh();
        }

        ArrayList<String> attributeValues = new ArrayList<>();
        for (Attribute a : t.getAttributes()) {
            attributeValues.add(a.getName());
        }

        ArrayList<String> datatypeValues = new ArrayList<>();
        for (Attribute a : t.getAttributes()) {
            String entry = a.getDatatyp().toString();
            if (a.isPrimaryKey()) {
                entry += " (Primary Key)";
            } else if (a.isForeignKey()) {
                entry += " (Foreign Key)";
            }
            datatypeValues.add(entry);
        }

        TableView<Integer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for (int i = 0; i < attributeValues.size(); i++) {
            table.getItems().add(i);
        }

        TableColumn<Integer, String> attributeColumn = new TableColumn<>("Attribute");
        attributeColumn.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(attributeValues.get(rowIndex));
        });

        TableColumn<Integer, String> datatypeColumn = new TableColumn<>("Datatype");
        datatypeColumn.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(datatypeValues.get(rowIndex));
        });

        table.getColumns().add(attributeColumn);
        table.getColumns().add(datatypeColumn);
        table.setEditable(true);

        tablePane.getChildren().clear();
        tablePane.getChildren().add(table);
    }

    public void setTable() {
        // Get Value of selected Item
        String curTbl = (String) selectTable.getValue();

        // If Value matches the Table's name --> Show it
        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                showTable(t);
                Logger.log(LogTags.INFO, "Controller.setTable(): Tabelle '" + t.getName() + "' angezeigt!");
            }
        }
        refresh();
    }

    public void addAttribute(ActionEvent actionEvent) {
        String attrName;
        Datatype datatyp;

        // Validate the Textfield and ComboBox
        if (attributeName.getText() != null && !attributeName.getText().equals("")) {
            attrName = attributeName.getText();
        } else {
            Components.simpleInfoBox("Info", "Attributename cannot be empty");
            return;
        }

        for (Attribute attrValid : selectedTable().getAttributes()) {
            if (attrName.equals(attrValid.getName())) {
                Components.simpleInfoBox("Info", "Attribute already exists!");
                attributeName.setText("");
                refresh();
                return;
            }
        }

        if (datatype.getValue() != null) {
            String dtype = (String) datatype.getValue();
            if (dtype.equals("Text")) {
                datatyp = Datatype.TEXT;
            } else if (dtype.equals("Decimal")) {
                datatyp = Datatype.DECIMAL;
            } else if (dtype.equals("Integer")) {
                datatyp = Datatype.INTEGER;
            } else {
                Components.simpleInfoBox("Info", "Please choose a datatype!");
                return;
            }
        } else {
            Components.simpleInfoBox("Info", "Please choose a datatype!");
            return;
        }

        // Create Attribute
        Attribute a = new Attribute(attrName, datatyp, isPrimary.isSelected(), isForeign.isSelected());

        // Set Foreignkey reference Values
        if (a.isForeignKey()) {
            ForeignKeyReference reference = foreignKeyInfoBox(allTables, selectedTable());
            Table forKeyT = reference.getReferencedTable();
            Attribute forKeyA = reference.getReferencedAttribute();
            a.setRefTable(forKeyT);
            a.setRefAttribute(forKeyA);

            Logger.log(LogTags.COMMENT, "Controller.addAttribute(): Referenced Table: '" + a.getRefTable().getName() + "' " +
                    "Attribute: '" + a.getRefAttribute().getName() + "'");
        }

        // Get Value of selected Item
        String curTbl = (String) selectTable.getValue();

        // Search correct Table in allTables
        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                t.addAttribute(a);
                Logger.log(LogTags.INFO, "Controller.addAttribute(): Der Tabelle '" + t.getName()
                        + "' wurde das Attribut '" + a.getName() + "' hinzugefügt!");
                showTable(t);
            }
        }

        attributeName.setText("");
        isForeign.setSelected(false);
        isPrimary.setSelected(false);
        isForeign.setDisable(false);
        isPrimary.setDisable(false);
        refresh();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        isPrimary.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (isPrimary.isSelected()) {
                    isForeign.setDisable(true);
                } else {
                    isForeign.setDisable(false);
                }
            }
        });

        isForeign.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (isForeign.isSelected()) {
                    isPrimary.setDisable(true);
                } else {
                    isPrimary.setDisable(false);
                }
            }
        });

        ObservableList<String> values2 = FXCollections.observableArrayList();
        values2.addAll("Text", "Decimal", "Integer");
        datatype.setItems(values2);

        ObservableList<String> values3 = FXCollections.observableArrayList();
        values3.addAll("HTML", "SQLite", "MySQL");
        wayOfExport.setItems(values3);

        checkTabStatus();
        refresh();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableName.requestFocus();
            }
        });
    }

    public void delete(ActionEvent actionEvent) {

        if (ok("Warning", "Delete table '" + selectTableDelete.getValue() + "' ?")) {

            if (selectTableDelete.getValue() != null) {
                String table = (String) selectTableDelete.getValue();

                for (Iterator<Table> iterator = allTables.iterator(); iterator.hasNext(); ) {
                    Table t = iterator.next();
                    if (t.getName().equals(table)) {
                        iterator.remove();
                        setTable();
                        Logger.log(LogTags.INFO, "Controller.delete(): Tabelle '" + t.getName() + "' gelöscht!");
                        refresh();
                    }
                }
                for (Iterator<Table> iterator = allTables.iterator(); iterator.hasNext(); ) {
                    Table t = iterator.next();
                    if (t.getName().equals(table)) {
                        iterator.remove();
                        setTable();
                        Logger.log(LogTags.INFO, "Controller.delete(): Tabelle '" + t.getName() + "' gelöscht!");
                        refresh();
                    }
                }

                dataPane.getChildren().clear();
                tablePane.getChildren().clear();
            } else {
                Components.simpleInfoBox("Info", "Please choose a table to delete!");
            }
            refresh();
            checkTabStatus();
        }
    }

    public void deleteAll(ActionEvent actionEvent) {
        if (Components.ok("Warning", "Delete all tables?")) {
            allTables.clear();
            refresh();
            dataPane.getChildren().clear();
            tablePane.getChildren().clear();
        }
        checkTabStatus();
    }

    @Nullable
    private Table selectedTable() {

        String curT = (String) selectTable.getValue();

        for (Table t : allTables) {
            if (t.getName().equals(curT)) {
                return t;
            }
        }
        return null;
    }

    public void deleteAttribute(ActionEvent actionEvent) {
        if (selectAttribute.getValue() != null) {
            String attrToDel = (String) selectAttribute.getValue();

            for (Iterator<Attribute> iterator = selectedTable().getAttributes().iterator(); iterator.hasNext(); ) {
                Attribute a = iterator.next();
                if (a.getName().equals(attrToDel)) {

                    if (a.isPrimaryKey()) {
                        for (Table t : allTables) {
                            for (Iterator<Attribute> attributeIterator = t.getAttributes().iterator(); attributeIterator.hasNext(); ) {
                                Attribute a1 = attributeIterator.next();
                                if (a1.isForeignKey()) {
                                    if (a1.getRefAttribute() == a) {
                                        if (ok("Warning", "Primary key is referenced by: " + a1.getName()
                                                + "This will delete the foreign key reference!")) {
                                            attributeIterator.remove();
                                        } else {
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    iterator.remove();
                    Logger.log(LogTags.INFO, "Controller.deleteAttribute(): Attribut '" + a.getName() + "' gelöscht!");
                    refresh();
                }
            }
        } else {
            Components.simpleInfoBox("Info", "Please choose an attribute to delete!");
        }
        selectAttribute.setItems(null);
        checkChoiceBoxes();
        refresh();
    }

    public void export(ActionEvent actionEvent) {
        if (wayOfExport.getValue() == null) {
            Components.simpleInfoBox("Info", "Choose a way of exporting!");
            return;
        }

        if (tableToExport.getValue() == null) {
            Components.simpleInfoBox("Info", "Choose a table to export!");
            return;
        }

        String curTbl = (String) tableToExport.getValue();
        Table tToExport = null;

        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                tToExport = t;
            }
        }

        String export = (String) wayOfExport.getValue();

        switch (export) {
            case "HTML":
                Exports.toHTML(tToExport);
                break;
            case "SQLite":
                Exports.toSQLite(tToExport);
                break;
            case "MySQL":
                Exports.toMySQL(tToExport);
                break;
        }

        refresh();
    }

    public void primarySelected(ActionEvent actionEvent) {

        String curTbl = (String) selectTable.getValue();

        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                if (isPrimary.isSelected() && t.hasPrimary() && t == selectedTable()) {
                    Components.simpleInfoBox("Info", "Table already has a Primary Key!");
                    isPrimary.setSelected(false);
                    return;
                }
            }
        }

        datatype.setValue("Integer");
        refresh();
    }


    public void foreignSelected(ActionEvent actionEvent) {
        String curTbl = (String) selectTable.getValue();

        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                if (isForeign.isSelected()) {

                    boolean hasPrimary = false;

                    for (Table tbl : allTables) {
                        if (tbl.hasPrimary() && tbl != t) {
                            hasPrimary = true;
                        }
                    }

                    if (!hasPrimary) {
                        Components.simpleInfoBox("Info", "There is no table with a primary key!");
                        isForeign.setSelected(false);
                        return;
                    }
                }
            }
        }

        datatype.setValue("Integer");
        refresh();
    }

    public void exportAll(ActionEvent actionEvent) {
        if (wayOfExport.getValue() == null) {
            Components.simpleInfoBox("Info", "Choose a way of exporting!");
            return;
        }

        if (allTables.size() == 0) {
            Components.simpleInfoBox("Info", "There are no tables yet!");
            return;
        }

        String export = (String) wayOfExport.getValue();

        switch (export) {
            case "HTML":
                Exports.toHTML(allTables);
                break;
            case "SQLite":
                Exports.toSQLite(allTables);
                break;
            case "MySQL":
                Exports.toMySQL(allTables);
                break;
        }

        refresh();
    }

    public void deactivateTabs(Event event) {
        checkTabStatus();
    }

    private void checkTabStatus() {
        if (allTables.size() == 0) {
            tabExport.setDisable(true);
            tabAlter.setDisable(true);
            tabDel.setDisable(true);
            tabInsert.setDisable(true);
            selectTable.setDisable(true);
        } else {
            tabExport.setDisable(false);
            tabAlter.setDisable(false);
            tabDel.setDisable(false);
            tabInsert.setDisable(false);
            selectTable.setDisable(false);
        }

        checkChoiceBoxes();
        checkInsertPossible();
    }

    private void checkChoiceBoxes() {
        if (selectedTable() != null) {
            if (selectedTable().getAttributes().size() == 0) {
                selectAttribute.setDisable(true);
                deleteAttribute.setDisable(true);
            } else {
                selectAttribute.setDisable(false);
                deleteAttribute.setDisable(false);
            }
        } else {
            selectAttribute.setDisable(true);
            deleteAttribute.setDisable(true);
        }
    }

    private void checkInsertPossible() {
        if (selectedTable() != null) {
            if (selectedTable().getAttributes().size() == 0) {
                newInsert.setDisable(true);
            } else {
                newInsert.setDisable(false);
            }
        }
    }

    public void newInsert(ActionEvent actionEvent) {
        Components.simpleInfoBox("Info", "Not implemented yet!");
    }

    public void showHelp(ActionEvent actionEvent) {
        new Help();
    }

    private class Help {

        private Stage tempStage;

        public Help() {
            Stage window = buildWindow("Help");
            Label l = new Label("Click to start!");
            l.setWrapText(true);

            Button createStage = new Button("Create tables");
            createStage.setPrefWidth(150);
            createStage.setOnAction(e -> {
                showTipsCreateTable();
                window.close();
            });

            Button modifyStage = new Button("Modify tables");
            modifyStage.setPrefWidth(150);
            modifyStage.setOnAction(e -> {
                showTipsModifyTable();
                window.close();
            });

            Button exportTables = new Button("Export tables");
            exportTables.setPrefWidth(150);
            exportTables.setOnAction(e -> {
                showTipsExportTable();
                window.close();
            });

            Button exitButton = new Button("Quit");
            exitButton.setPrefWidth(100);
            exitButton.setOnAction(e -> {
                window.close();
            });

            VBox layout = new VBox(15);
            layout.getChildren().addAll(l, createStage, modifyStage, exportTables, exitButton);
            layout.setAlignment(Pos.CENTER);

            layout.setOnKeyReleased(e ->
            {
                if (e.getCode() == KeyCode.ESCAPE)
                    window.close();
            });

            window.setScene(new Scene(layout, 350, 300));
            window.show();
        }

        private Stage buildWindow(String title) {
            tempStage = new Stage();
            tempStage.initStyle(StageStyle.UTILITY);
            tempStage.setResizable(false);
            tempStage.initModality(Modality.APPLICATION_MODAL);
            tempStage.setTitle(title);
            return tempStage;
        }
    }

    private void showTipsCreateTable() {

    }

    private void showTipsModifyTable() {

    }

    private void showTipsExportTable() {

    }
}
