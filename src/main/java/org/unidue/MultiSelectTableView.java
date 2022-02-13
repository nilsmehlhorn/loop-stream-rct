package org.unidue;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class MultiSelectTableView<S> extends TableView<S> {
    public MultiSelectTableView() {
        setup();
    }

    public MultiSelectTableView(ObservableList<S> observableList) {
        super(observableList);
        setup();
    }

    private void setup() {
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != this && !(node instanceof TableRow)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof TableRow) {
                // prevent further handling
                evt.consume();

                TableRow<S> row = (TableRow<S>) node;
                TableView<S> lv = row.getTableView();

                // focus the listview
                lv.requestFocus();

                if (!row.isEmpty()) {
                    // handle selection for non-empty cells
                    int index = row.getIndex();
                    if (row.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });
    }
}
