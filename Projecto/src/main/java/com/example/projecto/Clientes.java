package com.example.projecto;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Clientes implements Initializable {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    @FXML private TableView<ObservableList<String>> tablaClientes;
    @FXML private TableColumn<ObservableList<String>, String> colCodigo;
    @FXML private TableColumn<ObservableList<String>, String> colNombre;
    @FXML private TableColumn<ObservableList<String>, String> colCorreo;
    @FXML private TableColumn<ObservableList<String>, String> colTelefono;

    private ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Vincular columnas con los índices de la fila
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colCorreo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

        // Cargar datos iniciales
        cargarDatos();

        // Selección de fila
        seleccionarFila();
    }

    // ----------------------------
    // CARGAR DATOS DESDE LA BASE
    // ----------------------------
    private void cargarDatos() {
        lista.clear();
        tablaClientes.getItems().clear();

        DBconextion DB = new DBconextion();
        try (Connection conn = DB.getConexion()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT codigo, nombre, correo, telefono FROM clientes");

            while (rs.next()) {
                ObservableList<String> fila = FXCollections.observableArrayList();
                fila.add(rs.getString("codigo"));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("correo"));
                fila.add(rs.getString("telefono"));
                lista.add(fila);
            }

            tablaClientes.setItems(lista);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // AGREGAR NUEVO CLIENTE
    // ----------------------------
    @FXML
    private void agregarCliente() {
        // Validar campos
        if (!validarCampos()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Debe llenar todos los campos.", ButtonType.OK);
            alert.show();
            return;
        }

        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {
            String nuevoCodigo = "CLI-001";

            Statement stmt = conn.createStatement();
            ResultSet rsCodigo = stmt.executeQuery("SELECT codigo FROM Clientes ORDER BY id DESC LIMIT 1");

            if (rsCodigo.next()) {
                String ultimo = rsCodigo.getString("codigo");  // Ej: CLI-004
                int numero = Integer.parseInt(ultimo.split("-")[1]);
                numero++;
                nuevoCodigo = String.format("CLI-%03d", numero);
            }

            String sql = "INSERT INTO Clientes (codigo, nombre, correo, telefono) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoCodigo);
            ps.setString(2, nombre);
            ps.setString(3, correo);
            ps.setString(4, telefono);
            ps.executeUpdate();

            // =====================================
            // 3. CARGAR EL CLIENTE INSERTADO
            // =====================================
            ResultSet rs = stmt.executeQuery(
                    "SELECT codigo, nombre, correo, telefono FROM Clientes ORDER BY id DESC LIMIT 1"
            );

            if (rs.next()) {
                ObservableList<String> fila = FXCollections.observableArrayList(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                );

                lista.add(fila);
                tablaClientes.setItems(lista);
            }

            limpiar();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al agregar el cliente.", ButtonType.OK);
            alert.show();
        }
    }



    // ----------------------------
    // MODIFICAR CLIENTE
    // ----------------------------
    @FXML
    private void modificarCliente() {
        if (!validarCampos()) return;

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();

        DBconextion DB = new DBconextion();
        try (Connection conn = DB.getConexion()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE clientes SET nombre=?, correo=?, telefono=? WHERE codigo=?"
            );
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, telefono);
            ps.setString(4, codigo);
            ps.executeUpdate();

            cargarDatos();
            limpiar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // ELIMINAR CLIENTE
    // ----------------------------
    @FXML
    private void eliminarCliente() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) return;

        DBconextion DB = new DBconextion();
        try (Connection conn = DB.getConexion()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM clientes WHERE codigo=?");
            ps.setString(1, codigo);
            ps.executeUpdate();

            cargarDatos();
            limpiar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // SELECCIONAR FILA DE LA TABLA
    // ----------------------------
    private void seleccionarFila() {
        tablaClientes.setOnMouseClicked(event -> {
            ObservableList<String> fila = tablaClientes.getSelectionModel().getSelectedItem();
            if (fila != null) {
                txtCodigo.setText(fila.get(0));
                txtNombre.setText(fila.get(1));
                txtCorreo.setText(fila.get(2));
                txtTelefono.setText(fila.get(3));
            }
        });
    }

    // ----------------------------
    // VALIDAR CAMPOS
    // ----------------------------
    private boolean validarCampos() {
        return !txtCodigo.getText().isEmpty() &&
                !txtNombre.getText().isEmpty() &&
                !txtCorreo.getText().isEmpty() &&
                !txtTelefono.getText().isEmpty();
    }

    // ----------------------------
    // LIMPIAR CAMPOS
    // ----------------------------
    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
    }
}
