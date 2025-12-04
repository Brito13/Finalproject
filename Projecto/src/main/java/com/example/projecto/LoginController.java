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

public class LoginController {

    @FXML Button btn_iniciar;

    @FXML Button btn_cerrar;

    @FXML Label error;

    @FXML TextField txt_Usuario;

    @FXML PasswordField txt_Clave;


    public void iniciar(ActionEvent e) throws SQLException, IOException {
        if (txt_Usuario.getText().isEmpty() == false && txt_Clave.getText().isEmpty() == false){
            error.setText("Estas intentando iniciar seccion");
            validacionLogin(e);
        } else{
            error.setText("Ingrese su usuario y contraseña");
        }
    }

    public void cerrar(ActionEvent e){
        Stage stage = (Stage) btn_cerrar.getScene().getWindow();
        Stage stage2 = stage;
        stage2.close();
    }

    public void registrarview(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void validacionLogin(ActionEvent e) throws SQLException {
        DBconextion DB = new DBconextion();
        Connection conextionDB = DB.getConexion();


        try{
            String query = "SELECT COUNT(1) FROM Usuarios WHERE username = ? AND password = ?";
            PreparedStatement stmt = conextionDB.prepareStatement(query);
            stmt.setString(1, txt_Usuario.getText());
            stmt.setString(2, txt_Clave.getText());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                // Verificar si el usuario existe
                if (count > 0) {
                    error.setText("Correcto");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
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
                    error.setText("Incorrectos");
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }

}
