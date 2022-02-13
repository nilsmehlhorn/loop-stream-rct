package org.unidue;

import java.io.IOException;
import javafx.fxml.FXML;

public class StartController {

    @FXML
    private void start() throws IOException {
        App.setRoot("task");
    }
}
