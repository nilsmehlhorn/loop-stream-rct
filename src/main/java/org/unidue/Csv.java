package org.unidue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public class Csv {

    private final BufferedWriter out;

    public Csv() {
        DateTimeFormatter formatter = ofPattern("YY-MM-dd_HH_mm");
        try {
            out = new BufferedWriter(new FileWriter("exp_" + formatter.format(now()) + ".csv"));
            out.write(Run.csvHeader());
            out.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(Run run) {
        try {
            out.write(run.toCSV());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
