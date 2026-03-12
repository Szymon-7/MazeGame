module mazegame {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;

    exports mazegame;
    opens mazegame to com.fasterxml.jackson.databind;
}
