package org.unidue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SnippetResource {
    public final Integer number;
    public final URL url;

    public SnippetResource(Integer number, URL url) {
        this.number = number;
        this.url = url;
    }

    public BufferedReader read() {
        try {
            return new BufferedReader(new InputStreamReader(url.openStream(),
                    UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
