module org.unidue {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.base;
    requires org.commonmark;
    requires org.commonmark.ext.front.matter;
    requires com.google.gson;
    requires io.github.classgraph;

    opens org.unidue to javafx.fxml;
    exports org.unidue;
}
