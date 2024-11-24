package com.example.projecto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

public class Controller {

    @FXML Button btn_iniciar;

    public void setBtn_iniciar(ActiveEvent e){
        Stage stage = (Stage) btn_iniciar.getScene().getWindow();
        stage.close();
    }

}
