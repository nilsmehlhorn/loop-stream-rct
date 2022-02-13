package org.unidue;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

import static java.lang.Integer.parseInt;

public class RateController {

    @FXML
    public ToggleGroup performanceGroup;
    @FXML
    public ToggleGroup speedGroup;
    @FXML
    public ToggleGroup aestheticGroup;

    public void rate() {
        if (performanceGroup.getSelectedToggle() == null
                    || speedGroup.getSelectedToggle() == null
                    || aestheticGroup.getSelectedToggle() == null) {
            return;
        }
        Runs.instance().rateCurrent(
                parseInt(performanceGroup.getSelectedToggle().getUserData().toString()),
                parseInt(speedGroup.getSelectedToggle().getUserData().toString()),
                parseInt(aestheticGroup.getSelectedToggle().getUserData().toString())
        );
    }

}
