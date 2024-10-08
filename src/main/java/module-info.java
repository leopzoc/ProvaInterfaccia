module com.reti.progetto.provainterfaccia {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.reti.progetto.provainterfaccia to javafx.fxml;
    exports com.reti.progetto.provainterfaccia;
}