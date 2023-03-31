module com.example.projektwindyjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projektwindyjavafx to javafx.fxml;
    exports com.example.projektwindyjavafx;
}