package com.reti.progetto.provainterfaccia;

import java.net.Socket;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ControllerViewMain {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    private TextArea messageArea;

    // Metodo per impostare il socket e inizializzare gli stream
    public void initializeSocket(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            // Avvia il thread per ascoltare i messaggi in arrivo
            startListeningForMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo per ascoltare i messaggi dal server
    private void startListeningForMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message != null) {
                        updateMessageArea(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Errore nella ricezione dei messaggi.");
            }
        }).start();
    }

    // Metodo per aggiornare l'area messaggi nell'interfaccia utente
    private void updateMessageArea(String message) {
        // Assicura che l'aggiornamento avvenga sul thread JavaFX
        javafx.application.Platform.runLater(() -> {
            messageArea.appendText(message + "\n");
        });
    }

    // Metodo per inviare messaggi al server
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
