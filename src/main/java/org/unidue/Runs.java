package org.unidue;

import javafx.application.Platform;

import java.util.Iterator;
import java.util.List;

public class Runs {

    private static Runs instance;

    private final Csv csv = new Csv();

    private final Iterator<Run> runs = Snippets.instance()
                                               .getSnippets().stream()
                                               .map(Run::new)
                                               .iterator();

    private Run current = runs.next();

    public Run current() {
        return current;
    }

    public void finishCurrent(List<Integer> selected) {
        current.finish(selected);
        App.setRoot("rate");
    }

    public void rateCurrent(int performance, int speed, int aesthetic) {
        current.rate(performance, speed, aesthetic);
        csv.write(current);
        if (runs.hasNext()) {
            current = runs.next();
            App.setRoot("task");
        } else {
            csv.close();
            Platform.exit();
        }
    }

    public static Runs instance() {
        if (instance == null) {
            instance = new Runs();
        }
        return instance;
    }
}
