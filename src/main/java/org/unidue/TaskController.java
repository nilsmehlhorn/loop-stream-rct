package org.unidue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableList;

public class TaskController {

    @FXML
    private Label textLabel;
    @FXML
    private VBox inputsBox;
    @FXML
    private VBox outputsBox;
    @FXML
    private Button submitButton;

    private Node outputsView;

    private final Runs runs = Runs.instance();

    private final Run run = runs.current();

    private EventHandler<KeyEvent> keyListener;

    @FXML
    public void initialize() {
        HBox.setHgrow(inputsBox, Priority.SOMETIMES);
        HBox.setHgrow(outputsBox, Priority.SOMETIMES);
        HBox.setHgrow(inputsBox, Priority.SOMETIMES);
        textLabel.setText(run.snippet.text);
        inputsBox.getChildren().setAll(
                run.snippet.inputs.entrySet().stream().map(entry -> {
                    VBox listBox = new VBox();
                    listBox.setStyle("-fx-font-size: 14");
                    listBox.setSpacing(5);
                    Label label = new Label(entry.getKey());
                    label.setStyle("-fx-font-weight: bold");
                    Node node = jsonView(entry.getValue());
                    node.setStyle("-fx-accent: lightgrey; -fx-focus-color: transparent;");
                    listBox.getChildren().addAll(label, node);
                    return listBox;
                }).collect(toList())
        );
        submitButton.setText(String.format("Submit (%s/%d)", run.snippet.number, 14));
        outputsView = jsonView(run.snippet.outputs);
        outputsBox.getChildren().set(1, outputsView);
        keyListener = e -> {
            if (e.getCode() == KeyCode.P) {
                run.togglePause();
                boolean paused = run.isPaused();
                outputsBox.setDisable(paused);
                submitButton.setDisable(paused);
            }
        };
        App.scene.setOnKeyPressed(keyListener);
        run.start();
    }

    @FXML
    public void done() {
        List<Integer> selected;
        if (outputsView instanceof TableView) {
            selected = ((TableView) outputsView).getSelectionModel().getSelectedIndices();
        } else if (outputsView instanceof ListView) {
            selected = ((ListView) outputsView).getSelectionModel().getSelectedIndices();
        } else {
            throw new IllegalStateException("Cannot get selected outputs");
        }
        App.scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyListener);
        runs.finishCurrent(selected);
    }

    private static Node jsonView(List<JsonElement> elements) {
        JsonElement sample = elements.get(0);
        if (sample.isJsonPrimitive()) {
            ListView<String> list = new MultiSelectListView<>();
            VBox.setVgrow(list, Priority.NEVER);
            list.setItems(observableList(elements.stream().map(Object::toString).collect(toList())));
            return list;
        } else if (sample.isJsonObject()) {
            TableView<JsonObject> table = new MultiSelectTableView<>();
            JsonObject obj = (JsonObject) sample;
            table.getColumns().addAll(jsonColumns(obj, o -> o));
            table.getItems().addAll((Collection) elements);
            table.setFocusTraversable(false);
            return table;
        } else {
            throw new UnsupportedOperationException("Cannot display elements");
        }
    }

    private static List<TableColumn<JsonObject, String>> jsonColumns(JsonObject obj,
                                                                     Function<JsonObject, JsonObject> project) {
        List<TableColumn<JsonObject, String>> columns = new ArrayList<>();
        obj.entrySet().forEach(entry -> {
            TableColumn<JsonObject, String> col = new TableColumn<>(entry.getKey());
            if (entry.getValue().isJsonObject()) {
                col.getColumns().addAll(jsonColumns((JsonObject) entry.getValue(),
                        o -> (JsonObject) project.apply(o).get(entry.getKey())));
            } else {
                col.setCellValueFactory(param -> new ReadOnlyObjectWrapper(project.apply(param.getValue()).get(entry.getKey())));
            }
            columns.add(col);
        });
        return columns;
    }
}
