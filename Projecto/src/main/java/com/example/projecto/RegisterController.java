package com.example.projecto;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RegisterController {

    @FXML  TextField txt_Nombre;
    @FXML  TextField txt_Apellido;
    @FXML  TextField txt_Usuario;
    @FXML  PasswordField txt_Clave;
    @FXML  Label error;


    public void loginView(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Obtener el Stage actual desde el evento (desde el origen del evento)
        Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();

        // Crear una nueva escena con la vista cargada
        Scene scene = new Scene(root);

        // Cambiar la escena del escenario actual
        stage.setScene(scene);

        // Mostrar la nueva escena
        stage.show();
    }

    public void registrar(ActionEvent e){
        if (txt_Usuario.getText().isEmpty() == false && txt_Clave.getText().isEmpty() == false && txt_Apellido.getText().isEmpty() == false && txt_Nombre.getText().isEmpty() == false){
            registrarUsuario(e);
            error.setText("Registro realizado de manera exitosa");
        } else{
            error.setText("Los cambos estan vacios");
        }
    }

    public void registrarUsuario(ActionEvent e) {
        DBconextion DB = new DBconextion();
        Connection conextionDB = DB.getConexion();

        try {
            // Definir la consulta SQL para insertar un nuevo usuario
            String query = "INSERT INTO Usuarios (username, password,nombre,apellido) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conextionDB.prepareStatement(query);

            // Establecer los valores de los parámetros (username y password)
            stmt.setString(1, txt_Usuario.getText());
            stmt.setString(2, txt_Clave.getText());
            stmt.setString(3, txt_Nombre.getText());
            stmt.setString(4, txt_Apellido.getText());

            // Ejecutar la consulta de inserción
            int rowsAffected = stmt.executeUpdate();  // executeUpdate() para operaciones de inserción

            if (rowsAffected > 0) {
                // Si se inserta correctamente el usuario, mostrar un mensaje de éxito
                error.setText("Usuario registrado correctamente");

                // Cargar la vista de inicio (Home.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Parent root = loader.load();

                // Obtener el Stage actual desde el evento (desde el origen del evento)
                Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();

                // Crear una nueva escena con la vista cargada
                Scene scene = new Scene(root);

                // Cambiar la escena del escenario actual
                stage.setScene(scene);

                // Mostrar la nueva escena
                stage.show();
            } else {
                // Si no se insertaron filas, mostrar un mensaje de error
                error.setText("Error al registrar el usuario");
            }
        } catch (Exception exception) {
            error.setText("Error al registrar el usuario");
        }
    }
}
