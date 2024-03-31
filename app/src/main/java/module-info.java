module com.search {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens com.search to javafx.fxml;
    exports com.search;
}
