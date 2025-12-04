package com.example.projecto;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PeliculaController implements Initializable {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtGenero;
    @FXML private TextField txtFechaLanzamiento;
    @FXML private ComboBox<String> comboDisponibilidad;

    @FXML private TableView<ObservableList<String>> tablaPeliculas;
    @FXML private TableColumn<ObservableList<String>, String> colTitulo;
    @FXML private TableColumn<ObservableList<String>, String> colGenero;
    @FXML private TableColumn<ObservableList<String>, String> colFecha;
    @FXML private TableColumn<ObservableList<String>, String> colDisponibilidad;

    private ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Opciones del ComboBox
        comboDisponibilidad.getItems().addAll("Disponible", "No Disponible");
        comboDisponibilidad.setValue("Disponible");

        // Enlazar columnas
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colGenero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colDisponibilidad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

        // Cargar datos
        cargarDatos();

        // Activar selección
        seleccionarFila();
    }

    private void seleccionarFila() {
        tablaPeliculas.setOnMouseClicked(event -> {
            ObservableList<String> fila = tablaPeliculas.getSelectionModel().getSelectedItem();
            if (fila != null) {
                txtTitulo.setText(fila.get(0));
                txtGenero.setText(fila.get(1));
                txtFechaLanzamiento.setText(fila.get(2));

                if (fila.get(3).equals("1")) {
                    comboDisponibilidad.setValue("Disponible");
                } else {
                    comboDisponibilidad.setValue("No Disponible");
                }
            }
        });
    }

    private boolean validarCampos() {
        return !txtTitulo.getText().isEmpty() &&
                !txtGenero.getText().isEmpty() &&
                !txtFechaLanzamiento.getText().isEmpty() &&
                comboDisponibilidad.getValue() != null;
    }

    private void limpiar() {
        txtTitulo.clear();
        txtGenero.clear();
        txtFechaLanzamiento.clear();
        comboDisponibilidad.setValue("Disponible");
    }

    private void cargarDatos() {
        lista.clear();
        tablaPeliculas.getItems().clear();

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT titulo, genero, fecha_Lanzamiento, disponibilidad FROM Pelicula");

            while (rs.next()) {
                ObservableList<String> fila = FXCollections.observableArrayList();
                fila.add(rs.getString("titulo"));
                fila.add(rs.getString("genero"));
                fila.add(rs.getString("fecha_Lanzamiento"));

                // 🔥 Conversión de boolean (0/1) a texto amigable
                boolean disponible = rs.getBoolean("disponibilidad");
                fila.add(disponible ? "Disponible" : "No disponible");

                lista.add(fila);
            }

            tablaPeliculas.setItems(lista);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void agregarPelicula() {

        if (!validarCampos()) {
            System.out.println("Debe llenar todos los campos.");
            return;
        }

        String titulo = txtTitulo.getText().trim();
        String genero = txtGenero.getText().trim();
        String fecha = txtFechaLanzamiento.getText().trim();
        boolean disponible = comboDisponibilidad.getValue().equals("Disponible");

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            String query = "INSERT INTO Pelicula (titulo, genero, fecha_Lanzamiento, disponibilidad) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, titulo);
            ps.setString(2, genero);
            ps.setString(3, fecha);
            ps.setBoolean(4, disponible);
            ps.executeUpdate();

            cargarDatos();
            limpiar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void modificarPelicula() {

        ObservableList<String> fila = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (fila == null) return;

        String tituloAnterior = fila.get(0);

        String titulo = txtTitulo.getText().trim();
        String genero = txtGenero.getText().trim();
        String fecha = txtFechaLanzamiento.getText().trim();
        boolean disponible = comboDisponibilidad.getValue().equals("Disponible");

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            String query = "UPDATE Pelicula SET titulo=?, genero=?, fecha_Lanzamiento=?, disponibilidad=? WHERE titulo=?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, titulo);
            ps.setString(2, genero);
            ps.setString(3, fecha);
            ps.setBoolean(4, disponible);
            ps.setString(5, tituloAnterior);

            ps.executeUpdate();

            cargarDatos();
            limpiar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void eliminarPelicula() {

        ObservableList<String> fila = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (fila == null) return;

        String titulo = fila.get(0);

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            String query = "DELETE FROM Pelicula WHERE titulo=?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, titulo);
            ps.executeUpdate();

            cargarDatos();
            limpiar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
