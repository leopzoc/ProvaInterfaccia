package com.reti.progetto.provainterfaccia;
import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;

import javafx.application.Platform;

import javafx.scene.control.Alert;

import java.io.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.*;


public class LoginController {

    @FXML
    private TextField porta;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    private TextField ipField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private JFXButton signinButton;

    @FXML
    private Label ipErrorLabel;

    @FXML
    private Label statusLabel;

    private boolean alertShown = false;

    @FXML
    private ProgressIndicator loadingIndicator;


    @FXML
    public void initialize() {
        signinButton.setDisable(true);

        ipField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
        porta.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
    }

    private void checkForm() {
        boolean isFormComplete = !ipField.getText().isEmpty() && !usernameField.getText().isEmpty()
                && !passwordField.getText().isEmpty() && !porta.getText().isEmpty();

        boolean isIpValid = isValidIPv4(ipField.getText());
        boolean isPortValid = isValidPort(porta.getText());

        signinButton.setDisable(!(isFormComplete && isIpValid && isPortValid));

        if (!isIpValid && !ipField.getText().isEmpty()) {
            ipErrorLabel.setText("Formato IP non valido");
            ipErrorLabel.setVisible(true);
        } else if (!isPortValid && !porta.getText().isEmpty()) {
            ipErrorLabel.setText("Porta non valida (1-65535)");
            ipErrorLabel.setVisible(true);
        } else {
            ipErrorLabel.setVisible(false);
        }
    }

    private boolean isValidIPv4(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        return Pattern.matches(ipv4Pattern, ip);
    }

    private boolean isValidPort(String portText) {
        try {
            int port = Integer.parseInt(portText);
            return port > 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @FXML
    private void connect(ActionEvent event) {
        String ip = ipField.getText();
        int port = Integer.parseInt(porta.getText());
        String nick = usernameField.getText();
        String password = passwordField.getText();

        loadingIndicator.setVisible(true); // Mostra il caricamento

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 2000);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            String loginJson = createLoginJson(nick, password);
            sendMessage(loginJson);

            new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        String response = receiveMessage();
                        if (response != null) {
                            processServerResponse(response);
                        }
                    }
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        updateStatusLabel("Errore durante la ricezione del messaggio dal server.", "red");
                        loadingIndicator.setVisible(false); // Nasconde il caricamento in caso di errore
                    });
                }
            }).start();

        } catch (IOException ex) {
            updateStatusLabel("Connessione fallita a " + ip + ":" + port, "red");
            loadingIndicator.setVisible(false); // Nasconde il caricamento in caso di errore
        }
    }

    private void openConnection(String ip, int port) throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        socket = new Socket();
        loadingIndicator.setVisible(true);
        socket.connect(new InetSocketAddress(ip, port), 3000);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    private String createLoginJson(String nick, String password) {
        return String.format("{\"command\": \"login\", \"nick\": \"%s\", \"password\": \"%s\"}", nick, password);
    }

    private void sendMessage(String message) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + messageBytes.length);
        buffer.putInt(messageBytes.length);
        buffer.put(messageBytes);
        buffer.flip();
        out.write(buffer.array());
        out.flush();
    }

    private String receiveMessage() throws IOException {
        return in.readUTF();
    }

    private String extractErrorMessage(String response) {
        int startIndex = response.indexOf("\"message\":\"") + 11;
        int endIndex = response.indexOf("\"", startIndex);
        return response.substring(startIndex, endIndex);
    }


    private void processServerResponse(String response) {
        Platform.runLater(() -> {
            if (!alertShown) {
                if (response.contains("\"status\":\"success\"")) {
                    alertShown = true;
                    showAlert("Successo", "Login effettuato con successo!", Alert.AlertType.INFORMATION);
                    PauseTransition delay = new PauseTransition(Duration.seconds(2));
                    loadingIndicator.setVisible(false); // Nasconde il caricamento dopo la risposta
                    delay.setOnFinished(event -> loadMainView());
                    delay.play();
                } else if (response.contains("\"status\":\"error\"")) {
                    alertShown = true;
                    String errorMessage = extractErrorMessage(response);
                    showAlert("Errore", errorMessage, Alert.AlertType.ERROR);
                    try {
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reti/progetto/provainterfaccia/Main.fxml"));
            Parent mainView = loader.load();

            ControllerViewMain mainController = loader.getController();
            mainController.initializeSocket(socket);

            Stage currentStage = (Stage) signinButton.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(mainView));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            alertShown = false; // Reset alert state to allow another attempt if needed
        });
    }

    private void updateStatusLabel(String message, String color) {
        Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + ";");
        });
    }
}
