package globals;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CopyPasteBox {
    static Stage tempStage;

    private static Stage buildWindow (String title)
    {
        tempStage = new Stage();
        tempStage.initStyle(StageStyle.UTILITY);
        tempStage.setResizable(false);
        tempStage.initModality(Modality.APPLICATION_MODAL);
        tempStage.setTitle(title);
        return tempStage;
    }

    public CopyPasteBox(String title, String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);

        Stage window = buildWindow(title);
        TextArea ta = new TextArea(content);
        ta.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(ta);
        scrollPane.setFitToWidth(true);

        Button b = new Button("Copy to clipboard");

        b.setOnAction(e -> clipboard.setContent(clipboardContent));

        VBox layout = new VBox(15);
        layout.getChildren().addAll(scrollPane, b);
        layout.setAlignment(Pos.CENTER);

        Insets is = new Insets(0,10,0,10);
        layout.setPadding(is);

        layout.setOnKeyReleased(e ->
        {
            if (e.getCode() == KeyCode.ESCAPE)
                window.close();
        });

        window.setScene(new Scene(layout, 400, 300));
        window.show();
    }
}
