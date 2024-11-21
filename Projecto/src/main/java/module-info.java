module com.example.projecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.projecto to javafx.fxml;
    exports com.example.projecto;
}