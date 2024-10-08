package com.reti.progetto.provainterfaccia;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


import javafx.animation.FadeTransition;


import javafx.scene.Node;


public class LoginRegisterController implements Initializable {

    @FXML
    private VBox vbox;
    private Parent fxml;
    private double currentX = 0; // Posizione attuale della VBox

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Posiziona inizialmente la VBox fuori dal bordo destro del pannello principale
        vbox.setTranslateX(500); // Assicurati che corrisponda a targetX in Open_login
        loadLoginView(); // Carica la schermata di Login all'inizio

    }

    private void loadLoginView() {
        try {
            fxml = FXMLLoader.load(getClass().getResource("/com/reti/progetto/provainterfaccia/LoginFX.fxml"));
            vbox.getChildren().clear();
            vbox.getChildren().add(fxml);
        } catch (IOException ex) {
            Logger.getLogger(LoginRegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRegisterView() {
        try {
            fxml = FXMLLoader.load(getClass().getResource("/com/reti/progetto/provainterfaccia/Register.fxml"));
            vbox.getChildren().clear();
            vbox.getChildren().add(fxml);
        } catch (IOException ex) {
            Logger.getLogger(LoginRegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void Open_reg(ActionEvent event) {
        // Sposta la VBox alla posizione x = 0 per renderla visibile
        double targetX = 0;

        TranslateTransition t1 = new TranslateTransition(Duration.seconds(0.5), vbox);
        t1.setToX(targetX);
        t1.setOnFinished(e -> {
            // Dopo la transizione, inizia il fade out del contenuto corrente
            fadeOutContent(() -> {
                // Dopo il fade out, carica la nuova vista e fai il fade in
                loadRegisterView();
                fadeInContent();
            });
            currentX = targetX; // Aggiorna la posizione corrente
        });
        t1.play();
    }

    @FXML
    private void Open_login(ActionEvent event) {
        // Sposta la VBox fuori dallo schermo a destra
        double targetX = 500; // Assicurati che corrisponda a quanto definito in initialize

        TranslateTransition t1 = new TranslateTransition(Duration.seconds(0.5), vbox);
        t1.setToX(targetX);
        t1.setOnFinished(e -> {
            // Dopo la transizione, inizia il fade out del contenuto corrente
            fadeOutContent(() -> {
                // Dopo il fade out, carica la nuova vista e fai il fade in
                loadLoginView();
                fadeInContent();
            });
            currentX = targetX; // Aggiorna la posizione corrente
        });
        t1.play();
    }

    private void fadeOutContent(Runnable onFinished) {
        if (vbox.getChildren().isEmpty()) {
            onFinished.run();
            return;
        }
        Node content = vbox.getChildren().get(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), content);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(event -> onFinished.run());
        ft.play();
    }

    private void fadeInContent() {
        if (vbox.getChildren().isEmpty()) {
            return;
        }
        Node content = vbox.getChildren().get(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), content);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }


    @FXML
    private void tryOpenSocket(ActionEvent event) {


    }
}
