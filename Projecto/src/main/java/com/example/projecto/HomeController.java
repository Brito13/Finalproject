package com.example.projecto;

import com.example.projecto.Documentos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.print.Doc;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Label timeLabel;

    @FXML
    private Label dateLabel;

    private static Stage ventanaClientes = null;
    private static Stage ventanaRentas = null;
    private static Stage ventanaPeliculas = null;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLabelText();
    }

    private void loadLabelText() {
        LocalTime time = LocalTime.now();
        LocalDate today = LocalDate.now();

        // Dia en texto (lunes, martes, etc.)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", new Locale("es","ES"));
        String dayOfWeek = today.format(formatter);


        // Hora formateada
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime = time.format(timeFormatter);

        timeLabel.setText(formattedTime);
        dateLabel.setText(dayOfWeek);
    }




    public void GenerarReporte(ActionEvent e) throws IOException{
        Documentos doc = new Documentos();
        Documentos.generarReporteExcel();
    }


    public void Clientes(ActionEvent e) throws IOException{
        try {

            // VALIDACIÓN: si ya hay una ventana abierta → traer al frente y salir
            if (ventanaClientes != null && ventanaClientes.isShowing()) {
                ventanaClientes.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Clientes.fxml"));
            Parent root = loader.load();

            // Crear un nuevo Stage
            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle("Clientes");

            // Crear la escena
            Scene scene = new Scene(root);

            // Asignar la escena al nuevo Stage
            nuevaVentana.setScene(scene);

            // GUARDAR referencia para controlar la ventana
            ventanaClientes = nuevaVentana;

            // Cuando cierre la ventana, permitir abrir otra después
            nuevaVentana.setOnCloseRequest(event -> ventanaClientes = null);

            // Mostrar la nueva ventana sin cerrar la anterior
            nuevaVentana.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void Peliculas(ActionEvent e) throws IOException {
        try {
            if (ventanaPeliculas != null && ventanaPeliculas.isShowing()) {
                ventanaPeliculas.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Pelicula.fxml"));
            Parent root = loader.load();

            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle("Pelicula");

            Scene scene = new Scene(root);
            nuevaVentana.setScene(scene);

            ventanaPeliculas = nuevaVentana;

            nuevaVentana.setOnCloseRequest(event -> ventanaPeliculas = null);
            nuevaVentana.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Rentas(ActionEvent e) throws IOException {
        try {
            if (ventanaRentas!= null && ventanaRentas.isShowing()) {
                ventanaRentas.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Renta.fxml"));
            Parent root = loader.load();

            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle("Renta");

            Scene scene = new Scene(root);
            nuevaVentana.setScene(scene);

            ventanaRentas = nuevaVentana;

            nuevaVentana.setOnCloseRequest(event -> ventanaRentas = null);
            nuevaVentana.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}