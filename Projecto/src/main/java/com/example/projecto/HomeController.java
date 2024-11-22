package com.example.projecto;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomeController {

    @FXML
    private Label timeLabel;  // El Label donde se mostrará la hora
    @FXML
    private Label dateLabel;  // El Label donde se mostrará el día

    public void initialize() {
        // Crear un objeto Timeline para actualizar el reloj y la fecha cada segundo
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> updateDateTime()),  // Llamar a updateDateTime() al inicio
                new KeyFrame(Duration.seconds(1))  // Actualizar cada segundo
        );
        clock.setCycleCount(Timeline.INDEFINITE);  // El reloj y fecha se actualizan indefinidamente
        clock.play();  // Iniciar el reloj
    }

    // Método para actualizar tanto la hora como la fecha
    private void updateDateTime() {
        // Obtener la hora actual
        LocalTime time = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");  // Formato de la hora
        String formattedTime = time.format(timeFormatter);  // Formatear la hora

        // Obtener la fecha actual
        LocalDate date = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM");  // Formato de la fecha
        String formattedDate = date.format(dateFormatter);  // Formatear la fecha

        // Actualizar los Labels con los valores formateados
        timeLabel.setText(formattedTime);  // Actualizar la hora
        dateLabel.setText(formattedDate);  // Actualizar el día
    }
}
