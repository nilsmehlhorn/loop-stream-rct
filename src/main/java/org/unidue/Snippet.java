package org.unidue;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

public class Snippet {
    public final Integer number;
    public final String id;
    public final Type type;
    public final Map<String, List<JsonElement>> inputs;
    public final List<JsonElement> outputs;
    public final List<Integer> correctOutputs;
    public final String text;
    public final boolean focused;

    public Snippet(Integer number, String id, Type type, String text, Map<String, List<JsonElement>> inputs,
                   List<JsonElement> outputs,
                   List<Integer> correctOutputs, boolean focused) {
        this.number = number;
        this.id = id;
        this.type = type;
        this.inputs = inputs;
        this.outputs = outputs;
        this.text = text;
        this.correctOutputs = correctOutputs;
        this.focused = focused;
    }

    public enum Type {
        loop,
        stream;
    }
}
