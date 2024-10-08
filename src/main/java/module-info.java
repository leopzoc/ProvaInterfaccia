module com.reti.progetto.provainterfaccia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.graphics;
    requires javafx.base;
    requires com.jfoenix;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.desktop;

    opens com.reti.progetto.provainterfaccia to javafx.fxml;
    exports com.reti.progetto.provainterfaccia;
}
