package sample;

import com.sun.istack.internal.Nullable;
import globals.Components;
import globals.*;
import tables.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import tables.Attribute;
import tables.Datatype;
import tables.Exports;
import tables.Table;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import static globals.Components.foreignKeyInfoBox;

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
    private ArrayList<Table> allTables = new ArrayList<>();

    public void createTable(ActionEvent actionEvent) {
        // If Text is not empty, then:
        if (tableName.getText() != null && !tableName.getText().equals("")) {
            // Create new Instance Table
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
            tablePane.getChildren().clear();

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
            tablePane.getChildren().add(table);
            refresh();
        }
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

            Logger.log(LogTags.COMMENT, "Controller.addAttribute(): Referenced Table: '"+a.getRefTable().getName()+"' "+
                    "Attribute: '"+a.getRefAttribute().getName() + "'");
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
        ObservableList<String> values2 = FXCollections.observableArrayList();

        // Add values to OberservableList values
        values2.addAll("Text", "Decimal", "Integer");

        // Set Value to ComboBox selectTable
        datatype.setItems(values2);

        ObservableList<String> values3 = FXCollections.observableArrayList();

        values3.addAll("HTML", "SQLite", "MySQL");

        wayOfExport.setItems(values3);
        refresh();
    }

    public void delete(ActionEvent actionEvent) {
        if (selectTableDelete.getValue() != null) {
            String table = (String) selectTableDelete.getValue();

            for (Iterator<Table> iterator = allTables.iterator(); iterator.hasNext();) {
                Table t = iterator.next();
                if (t.getName().equals(table)) {
                    iterator.remove();
                    setTable();
                    Logger.log(LogTags.INFO, "Controller.delete(): Tabelle '" + t.getName() + "' gelöscht!");
                    refresh();
                }
            }for (Iterator<Table> iterator = allTables.iterator(); iterator.hasNext();) {
                Table t = iterator.next();
                if (t.getName().equals(table)) {
                    iterator.remove();
                    setTable();
                    Logger.log(LogTags.INFO, "Controller.delete(): Tabelle '" + t.getName() + "' gelöscht!");
                    refresh();
                }
            }

            tablePane.getChildren().clear();
        } else {
            Components.simpleInfoBox("Info", "Please choose a table to delete!");
        }
        refresh();
    }

    public void deleteAll(ActionEvent actionEvent) {
        if (Components.ok("Warning","Delete all tables?")) {
            allTables.clear();
            refresh();
            tablePane.getChildren().clear();
        }
    }

    @Nullable
    private Table selectedTable() {

        String curT = (String)selectTable.getValue();

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
                    iterator.remove();
                    Logger.log(LogTags.INFO, "Controller.deleteAttribute(): Attribut '" + a.getName() + "' gelöscht!");
                    refresh();
                }
            }
        } else {
            Components.simpleInfoBox("Info", "Please choose an attribute to delete!");
        }
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

        String curTbl = (String)tableToExport.getValue();
        Table tToExport = null;

        for (Table t : allTables) {
            if (t.getName().equals(curTbl)) {
                tToExport = t;
            }
        }

        String export = (String)wayOfExport.getValue();

        switch(export) {
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
                if (isPrimary.isSelected() && t.hasPrimary()) {
                    Components.simpleInfoBox("Info", "Table already has a Primary Key!");
                    isPrimary.setSelected(false);
                    isForeign.setDisable(false);
                    return;
                }
            }
        }

        datatype.setValue("Integer");
        isForeign.setDisable(true);
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
                        isPrimary.setDisable(false);
                        return;
                    }
                }
            }
        }

        datatype.setValue("Integer");
        isPrimary.setDisable(true);
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

        String export = (String)wayOfExport.getValue();

        switch(export) {
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
}
