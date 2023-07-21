module com.example.boattravelling {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.mail;


    opens com.example.boattravelling to javafx.fxml;
    exports com.example.boattravelling;
}