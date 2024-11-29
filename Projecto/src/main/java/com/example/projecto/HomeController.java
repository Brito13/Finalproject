package com.example.projecto;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import java.io.IOException;


import java.net.URL;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;


public class HomeController extends Application {


    @FXML
     Label timeLabel;  // El Label donde se mostrará la hora
    @FXML
     Label dateLabel;  // El Label donde se mostrará el día

    @FXML
    private Button bttnconection; // Boton para acceder vista reportes John

    public void initialize() {
        loadLabelText();
    }

    private void loadLabelText() {
        // Obtener la hora actual
        LocalTime time = LocalTime.now();
        LocalDate today = LocalDate.now();

        // Definir el formato para el día de la semana
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");

        // Formatear la fecha para obtener el nombre completo del día
        String dayOfWeek = today.format(formatter);


        // Definir el formato de la hora
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");  // Puedes personalizar el formato
        String formattedTime = time.format(timeFormatter);  // Formatear la hora en el formato deseado

        // Establecer el texto del label con la hora actual
        timeLabel.setText(formattedTime);
        dateLabel.setText(dayOfWeek);
    }

    @FXML
    private void handleSwitchView(ActionEvent event) throws IOException {
        try{
            HBox secondaryView = FXMLLoader.load(getClass().getResource("Reports.fxml"));

            Scene secondaryScene = new Scene(secondaryView);

            Stage stage = (Stage) bttnconection.getScene().getWindow();

            stage.setScene(secondaryScene);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

    }
}