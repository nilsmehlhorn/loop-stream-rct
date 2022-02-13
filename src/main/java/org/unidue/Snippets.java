package org.unidue;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Snippets {

    private static Snippets instance;

    private Parser parser = Parser.builder()
                                    .extensions(List.of(YamlFrontMatterExtension.create()))
                                    .build();

    private TextContentRenderer renderer = TextContentRenderer.builder().build();

    private List<Snippet> snippets = new ArrayList<>();

    public void init(String batch) {
        List<Snippet> snippets = classpathFiles("snippets/batch_" + batch).stream()
                                         .map(this::parseSnippet)
                                         .sorted(comparing(snippet -> snippet.number))
                                         .collect(toList());
        List<Snippet> focused = snippets.stream().filter(snippet -> snippet.focused).collect(toList());
        if (focused.isEmpty()) {
            this.snippets = snippets;
        } else {
            this.snippets = focused;
        }
    }

    private List<JsonElement> parseElements(List<String> raw) {
        return raw.stream().map(JsonParser::parseString).collect(toList());
    }

    public List<Snippet> getSnippets() {
        return snippets;
    }

    public static Snippets instance() {
        if (instance == null) {
            instance = new Snippets();
        }
        return instance;
    }

    private Snippet parseSnippet(SnippetResource resource) {
        try (BufferedReader input = resource.read()) {
            Node node = parser.parseReader(input);
            String text = renderer.render(node);
            YamlFrontMatterVisitor yaml = new YamlFrontMatterVisitor();
            node.accept(yaml);
            Map<String, List<String>> data = yaml.getData();
            Map<String, List<JsonElement>> inputs = data.entrySet().stream()
                                                            .filter(entry -> entry.getKey().startsWith("input_"))
                                                            .collect(toMap(
                                                                    entry -> entry.getKey()
                                                                                     .replace("input_", ""),
                                                                    entry -> parseElements(entry.getValue())
                                                            ));
            List<JsonElement> outputs = parseElements(data.get("outputs"));
            List<Integer> correctOutputs = data.get("correct_outputs").stream()
                                                   .map(Integer::parseInt).collect(toList());
            Snippet.Type type = Snippet.Type.valueOf(data.get("type").get(0).trim());
            String id = data.get("id").get(0).trim();
            boolean focused = ofNullable(data.get("focused"))
                                      .map(l -> parseBoolean(l.get(0))).orElse(false);
            return new Snippet(resource.number, id, type, text, inputs, outputs, correctOutputs, focused);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SnippetResource> classpathFiles(String path) {
        List<SnippetResource> snippets = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().whitelistPaths("org/unidue/" + path).scan()) {
            List<Resource> resources = scanResult.getResourcesWithExtension("md");
            for (int i = 0; i < resources.size(); i++) {
                String resourcePath = resources.get(i).getPath().replace("org/unidue/", "");
                URL resource = App.class.getResource(resourcePath);
                Integer number = parseInt(fileNameWithOutExt(resourcePath.replace(path + "/", "")));
                snippets.add(new SnippetResource(number, resource));
            }
        }
        return snippets;
    }

    public static String fileNameWithOutExt(String fileName) {
        return Optional.of(fileName.lastIndexOf(".")).filter(i -> i >= 0)
                       .map(i -> fileName.substring(0, i)).orElse(fileName);
    }
}
