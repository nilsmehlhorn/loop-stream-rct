package org.unidue;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Run {

    public final Snippet snippet;

    private long startTime;

    private long finishTime;

    private boolean correct;

    private boolean finished = false;

    private int performanceRating;
    private int speedRating;
    private int aestheticRating;

    private Long pause;

    public Run(Snippet snippet) {
        this.snippet = snippet;
    }

    void start() {
        if (finished) {
            throw new IllegalStateException("Run already finished");
        }
        this.startTime = System.currentTimeMillis();
    }

    void togglePause() {
        if (pause == null) {
            pause = System.currentTimeMillis();
        } else {
            this.startTime += System.currentTimeMillis() - pause;
            pause = null;
        }
    }

    boolean isPaused() {
        return pause != null;
    }

    void finish(List<Integer> answers) {
        if (pause != null) {
            throw new IllegalStateException("Run is paused, unpause first to finish");
        }
        if (finished) {
            throw new IllegalStateException("Run already finished");
        }
        this.finishTime = System.currentTimeMillis();
        this.correct = answers.size() == this.snippet.correctOutputs.size()
                               && answers.containsAll(this.snippet.correctOutputs);
        finished = true;
    }

    void rate(int performance, int speed, int aesthetic) {
        performanceRating = performance;
        speedRating = speed;
        aestheticRating = aesthetic;
    }

    String toCSV() {
        if (!finished) {
            throw new IllegalStateException("Cannot export unfinished run");
        }
        return Stream.of(
                this.snippet.id,
                this.snippet.type.ordinal(),
                this.timeTaken(),
                intBool(this.correct),
                this.performanceRating,
                this.speedRating,
                this.aestheticRating
        ).map(String::valueOf).collect(joining(","));
    }

    long timeTaken() {
        return finishTime - startTime;
    }

    static String csvHeader() {
        return String.join(",",
                "ID",
                "Type",
                "TimeInMS",
                "Correct",
                "PerformanceRating",
                "SpeedRating",
                "AestheticRating"
        );
    }

    private static int intBool(boolean bool) {
        return bool ? 1 : 0;
    }
}
