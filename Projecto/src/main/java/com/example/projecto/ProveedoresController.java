package com.example.projecto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ProveedoresController {
    @FXML private TextField nombreField;
    @FXML private TextField contactoField;
    @FXML private TextField direccionField;
    @FXML private TableView<Proveedor> proveedoresTable;
    @FXML private TableColumn<Proveedor, Integer> idColumn;
    @FXML private TableColumn<Proveedor, String> nombreColumn;
    @FXML private TableColumn<Proveedor, String> contactoColumn;
    @FXML private TableColumn<Proveedor, String> direccionColumn;
    @FXML private Button btnvolverHome;

    private ProveedorDAO proveedorDAO;
    private ObservableList<Proveedor> proveedoresList;

    public void initialize() {
        proveedorDAO = new ProveedorDAO();
        proveedoresList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        nombreColumn.setCellValueFactory(data -> data.getValue().nombreProperty());
        contactoColumn.setCellValueFactory(data -> data.getValue().contactoProperty());
        direccionColumn.setCellValueFactory(data -> data.getValue().direccionProperty());

        cargarProveedores();
        proveedoresTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Cargar datos del proveedor seleccionado en los campos de texto
                cargarCamposProveedor(newValue);
            }
        });
    }

    private void cargarProveedores() {
        try {
            ObservableList<Proveedor> proveedoresList = FXCollections.observableArrayList(proveedorDAO.listarProveedores());
            proveedoresTable.setItems(proveedoresList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarCamposProveedor(Proveedor proveedor) {
        // Cargar los datos del proveedor seleccionado en los campos de texto
        nombreField.setText(proveedor.getNombre());
        contactoField.setText(proveedor.getContacto());
        direccionField.setText(proveedor.getDireccion());
    }

    @FXML
    private void agregarProveedor() {
        String nombre = nombreField.getText();
        String contacto = contactoField.getText();
        String direccion = direccionField.getText();

        if (nombre.isEmpty() || contacto.isEmpty() || direccion.isEmpty()) {
            showMessage("Todos los campos son obligatorios.");
        } else {
            Proveedor proveedor = new Proveedor(0, nombre, contacto, direccion);
            try {
                if (proveedorDAO.proveedorExistente(nombre, contacto)) {
                    showMessage("Este proveedor ya existe en la base de datos.");
                    return;
                }
                proveedorDAO.agregarProveedor(proveedor);
                proveedoresList.clear(); // Limpiar la lista de la tabla
                cargarProveedores(); // Actualizar la tabla después de agregar
                showMessage("Proveedor agregado exitosamente.");

                // Limpiar los campos de texto
                nombreField.clear();
                contactoField.clear();
                direccionField.clear();

            } catch (SQLException e) {
                showMessage("Error al agregar el proveedor.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void editarProveedor() {
        Proveedor proveedorSeleccionado = proveedoresTable.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado != null) {
            // Obtener los datos actualizados desde los campos de texto
            String nombre = nombreField.getText();
            String contacto = contactoField.getText();
            String direccion = direccionField.getText();


            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || contacto.isEmpty() || direccion.isEmpty()) {
                showMessage("Todos los campos son obligatorios.");
            } else {
                // Actualizar el proveedor en la base de datos
                proveedorSeleccionado.setNombre(nombre);
                proveedorSeleccionado.setContacto(contacto);
                proveedorSeleccionado.setDireccion(direccion);

                try {
                    proveedorDAO.editarProveedor(proveedorSeleccionado);
                    cargarProveedores(); // Actualizar la tabla después de editar
                    showMessage("Proveedor editado exitosamente.");

                    // Limpiar los campos de texto
                    nombreField.clear();
                    contactoField.clear();
                    direccionField.clear();
                } catch (SQLException e) {
                    showMessage("Error al editar el proveedor.");
                    e.printStackTrace();
                }
            }
        } else {
            showMessage("Por favor, selecciona un proveedor para editar.");
        }
    }

    @FXML
    public void eliminarProveedor() {

        Proveedor proveedorSeleccionado = proveedoresTable.getSelectionModel().getSelectedItem();
        if (proveedorSeleccionado != null) {
            try {
                if (proveedorDAO.facturasAsociadas(proveedorSeleccionado.getId())) {
                    showMessage("No se puede eliminar el proveedor, tiene facturas asociadas.");
                    return;
                }
                proveedorDAO.eliminarProveedor(proveedorSeleccionado.getId());
                proveedoresList.remove(proveedorSeleccionado);
                cargarProveedores();
                showMessage("Proveedor eliminado exitosamente.");
                nombreField.clear();
                contactoField.clear();
                direccionField.clear();

            } catch (SQLException e) {
                showMessage("Error al eliminar el proveedor.");
                e.printStackTrace();
            }
        } else {
            showMessage("Por favor, selecciona un proveedor para eliminar.");
        }
    }

    // Mostrar mensaje en un Alert
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void volverHome(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Stage stage = (Stage) btnvolverHome.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();
        }catch (IOException excep){
            excep.printStackTrace();
        }
    }
}
