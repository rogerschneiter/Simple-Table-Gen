package sample;

import globals.Components;
import globals.ForeignKeyReference;
import globals.LogTags;
import globals.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import tables.Attribute;
import tables.Datatype;
import tables.Exports;
import tables.Table;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static globals.Components.foreignKeyInfoBox;
import static globals.Components.ok;

public class Controller implements Initializable {

    // Declaration of Variables
    public TextField tableName;
    public ComboBox<String> selectTable;
    public TextField attributeName;
    public ComboBox<String> datatype;
    public ComboBox<String> selectTableDelete;
    public ComboBox<String> selectAttribute;
    public ComboBox<String> tableToExport;
    public ComboBox<String> wayOfExport;
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
    public Label helpTextCreate;
    public TabPane tabPane;
    public Button addButton;
    public Button exportButton;
    public Label exportHelpText;
    private ArrayList<Table> allTables = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        isPrimary.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (isPrimary.isSelected()) {
                isForeign.setDisable(true);
                datatype.setDisable(true);
            } else {
                isForeign.setDisable(false);
                datatype.setDisable(false);
            }
        });

        isForeign.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (isForeign.isSelected()) {
                isPrimary.setDisable(true);
                datatype.setDisable(true);
            } else {
                isPrimary.setDisable(false);
                datatype.setDisable(false);
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

        Platform.runLater(() -> tableName.requestFocus());
    }

    private ChangeListener<? super String> tableChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (newValue != null && !newValue.equals("")) {
                tableName.setStyle("-fx-border-color: none");
                tableName.setPromptText("Table");
                createTable.setStyle("-fx-border-color: red");
                helpTextCreate.setText("<-- Click here to create table!");
            }
        }
    };

    private ChangeListener<? super String> datatypeChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            datatype.setStyle("-fx-border-color: none");
            addButton.setStyle("-fx-border-color: red");
        }
    };

    private ChangeListener<? super String> attrChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            attributeName.setPromptText("Attribute");
            attributeName.setStyle("-fx-border-color: none");
            datatype.setStyle("-fx-border-color: red");
        }
    };

    private ChangeListener<? super String> wayOfExportChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            tableToExport.valueProperty().removeListener(exportTableChangListener);
            wayOfExport.setStyle("-fx-border-color: none");
            exportButton.setStyle("-fx-border-color: red");
            exportHelpText.setText("<-- Click to export all tables!");
        }
    };

    private ChangeListener<? super String> exportTableChangListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            tableToExport.setStyle("-fx-border-color: none");
            wayOfExport.setStyle("-fx-border-color: red");
            wayOfExport.valueProperty().addListener(wayOfExportChangeListener);
        }
    };

    private ChangeListener<? super Tab> changeTabWhileHelpListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            killHelp();
            tabPane.getSelectionModel().selectedItemProperty().removeListener(changeTabWhileHelpListener);
        }
    };

    private void killHelp() {
        // Kill everything from Table Create Help
        helpTextCreate.setText("");
        createTable.setStyle("-fx-border-color: none");
        tableName.setStyle("-fx-border-color: none");
        tableName.textProperty().removeListener(tableChangeListener);

        // Kill everything from Modify Table Help
        attributeName.textProperty().removeListener(attrChangeListener);
        datatype.valueProperty().removeListener(datatypeChangeListener);
        attributeName.setStyle("-fx-border-color: none");
        addButton.setStyle("-fx-border-color: none");
        attributeName.setText("");

        // Kill everything from Export Table Help
        wayOfExport.valueProperty().removeListener(wayOfExportChangeListener);
        tableToExport.valueProperty().removeListener(exportTableChangListener);
        wayOfExport.setStyle("-fx-border-color: none");
        tableToExport.setStyle("-fx-border-color: none");
        exportButton.setStyle("-fx-border-color: none");
        exportHelpText.setText("");
    }

    public void createTable() {
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

        tabPane.getSelectionModel().selectedItemProperty().removeListener(changeTabWhileHelpListener);
        helpTextCreate.setText("");
        createTable.setStyle("-fx-border-color: none");
        tableName.textProperty().removeListener(tableChangeListener);
        checkTabStatus();
        refresh();
    }

    private void refresh() {
        ObservableList<String> values = FXCollections.observableArrayList();
        values.clear();

        // Add values to OberservableList values
        values.addAll(allTables.stream().map(Table::getName).collect(Collectors.toList()));

        // Set Value to ComboBox selectTable and selectTableDelete
        selectTable.setItems(values);
        selectTableDelete.setItems(values);
        tableToExport.setItems(values);

        // Add values to second Obs List
        ObservableList<String> values2 = FXCollections.observableArrayList();
        values2.clear();

        if (selectedTable() != null) {
            values2.addAll(selectedTable().getAttributes().stream().map(Attribute::getName).collect(Collectors.toList()));
        }

        selectAttribute.setItems(values2);
    }

    private void showTable(Table t) {
        {
            // clear tablePane
            dataPane.getChildren().clear();

            // Create new TableView
            TableView<String> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // For each Attribute add one Column with correct name
            for (Attribute a : t.getAttributes()) {
                javafx.scene.control.TableColumn<String, String> col1 = new javafx.scene.control.TableColumn<>(a.getName());
                col1.setPrefWidth(a.getName().length() * 15);
                table.getColumns().add(col1);
            }

            // Add table to tablePane
            dataPane.getChildren().add(table);
            refresh();
        }

        ArrayList<String> attributeValues = t.getAttributes().stream().map(Attribute::getName).collect(Collectors.toCollection(ArrayList::new));

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
        String curTbl = selectTable.getValue();

        // If Value matches the Table's name --> Show it
        allTables.stream().filter(t -> t.getName().equals(curTbl)).forEach(t -> {
            showTable(t);
            Logger.log(LogTags.INFO, "Controller.setTable(): Tabelle '" + t.getName() + "' angezeigt!");
        });
        refresh();
    }

    public void addAttribute() {
        String attrName;
        Datatype datatyp;

        attributeName.textProperty().removeListener(attrChangeListener);
        datatype.valueProperty().removeListener(datatypeChangeListener);

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
            String dtype = datatype.getValue();
            switch (dtype) {
                case "Text":
                    datatyp = Datatype.TEXT;
                    break;
                case "Decimal":
                    datatyp = Datatype.DECIMAL;
                    break;
                case "Integer":
                    datatyp = Datatype.INTEGER;
                    break;
                default:
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
        String curTbl = selectTable.getValue();

        // Search correct Table in allTables
        allTables.stream().filter(t -> t.getName().equals(curTbl)).forEach(t -> {
            t.addAttribute(a);
            Logger.log(LogTags.INFO, "Controller.addAttribute(): Der Tabelle '" + t.getName()
                    + "' wurde das Attribut '" + a.getName() + "' hinzugefügt!");
            showTable(t);
        });

        tabPane.getSelectionModel().selectedItemProperty().removeListener(changeTabWhileHelpListener);
        attributeName.setStyle("-fx-border-color: none");
        addButton.setStyle("-fx-border-color: none");
        attributeName.setText("");
        isForeign.setSelected(false);
        isPrimary.setSelected(false);
        isForeign.setDisable(false);
        isPrimary.setDisable(false);
        refresh();
    }

    public void delete() {

        if (ok("Warning", "Delete table '" + selectTableDelete.getValue() + "' ?")) {

            if (selectTableDelete.getValue() != null) {
                String table = selectTableDelete.getValue();

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

    public void deleteAll() {
        if (Components.ok("Warning", "Delete all tables?")) {
            allTables.clear();
            refresh();
            dataPane.getChildren().clear();
            tablePane.getChildren().clear();
        }
        checkTabStatus();
    }

    private Table selectedTable() {

        Table tbl = null;
        String curT = selectTable.getValue();

        for (Table t : allTables) {
            if (t.getName().equals(curT)) {
                tbl = t;
            }
        }
        return tbl;
    }

    public void deleteAttribute() {
        if (selectAttribute.getValue() != null) {
            String attrToDel = selectAttribute.getValue();

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

    public void export() {
        wayOfExport.valueProperty().removeListener(wayOfExportChangeListener);

        if (wayOfExport.getValue() == null) {
            Components.simpleInfoBox("Info", "Choose a way of exporting!");
            return;
        }

        if (tableToExport.getValue() == null) {
            Components.simpleInfoBox("Info", "Choose a table to export!");
            return;
        }

        String curTbl = tableToExport.getValue();
        Table tToExport = null;

        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                tToExport = t;
            }
        }

        String export = wayOfExport.getValue();

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

        tabPane.getSelectionModel().selectedItemProperty().removeListener(changeTabWhileHelpListener);
        wayOfExport.setStyle("-fx-border-color: none");
        exportButton.setStyle("-fx-border-color: none");
        exportHelpText.setText("");
        refresh();
    }

    public void primarySelected() {

        String curTbl = selectTable.getValue();

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

    public void foreignSelected() {
        String curTbl = selectTable.getValue();

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

    public void exportAll() {
        if (wayOfExport.getValue() == null) {
            Components.simpleInfoBox("Info", "Choose a way of exporting!");
            return;
        }

        if (allTables.size() == 0) {
            Components.simpleInfoBox("Info", "There are no tables yet!");
            return;
        }

        String export = wayOfExport.getValue();

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

        tabPane.getSelectionModel().selectedItemProperty().removeListener(changeTabWhileHelpListener);
        tableToExport.valueProperty().removeListener(exportTableChangListener);
        wayOfExport.valueProperty().removeListener(wayOfExportChangeListener);
        wayOfExport.setStyle("-fx-border-color: none");
        exportButton.setStyle("-fx-border-color: none");
        exportHelpText.setText("");
        refresh();
    }

    public void deactivateTabs() {
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

    public void newInsert() {
        Components.simpleInfoBox("Info", "Not implemented yet!");
    }

    public void showHelp() {
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
            exitButton.setOnAction(e -> window.close());

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
        SingleSelectionModel<Tab> tabModel = tabPane.getSelectionModel();
        tabModel.select(0);
        tableName.setStyle("-fx-border-color: red");
        tableName.setPromptText("Enter table name here!");
        tableName.textProperty().addListener(tableChangeListener);

        tabPane.getSelectionModel().selectedItemProperty().addListener(changeTabWhileHelpListener);
    }

    private void showTipsModifyTable() {
        if (allTables.size() != 0) {
            SingleSelectionModel<Tab> tabModel = tabPane.getSelectionModel();
            tabModel.select(1);
            attributeName.setStyle("-fx-border-color: red");
            attributeName.setPromptText("Enter attribute name here!");
            attributeName.textProperty().addListener(attrChangeListener);

            datatype.valueProperty().addListener(datatypeChangeListener);
            tabPane.getSelectionModel().selectedItemProperty().addListener(changeTabWhileHelpListener);

        } else {
            if (ok("Info", "No tables yet. Start tutorial on how to create a table?")) {
                showTipsCreateTable();
            }
        }
    }

    private void showTipsExportTable() {
        if (allTables.size() != 0) {
            tableToExport.setValue(null);
            SingleSelectionModel<Tab> tabModel = tabPane.getSelectionModel();
            tabModel.select(3);
            tableToExport.setStyle("-fx-border-color: red");
            tableToExport.valueProperty().addListener(exportTableChangListener);
            tabPane.getSelectionModel().selectedItemProperty().addListener(changeTabWhileHelpListener);
        } else {
            if (ok("Info", "No tables yet. Start tutorial on how to create a table?")) {
                showTipsCreateTable();
            }
        }
    }
}
