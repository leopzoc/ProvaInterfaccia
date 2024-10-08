package com.reti.progetto.provainterfaccia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginRegisterApplication extends Application {

    @Override
    public void start(Stage LoginStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene sceneLogin = new Scene(root);
        sceneLogin.setFill(Color.TRANSPARENT);
        LoginStage.setScene(sceneLogin);
        LoginStage.setTitle("Provainter Faccia");
        LoginStage.initStyle(StageStyle.TRANSPARENT);
        LoginStage.show();

    }
}
