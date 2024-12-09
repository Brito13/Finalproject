package com.example.projecto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FacturasController{
    @FXML private TextField numeroFacturaField;
    @FXML private DatePicker fechaField;
    @FXML private TextField montoField;
    @FXML private TableView<Factura> facturasTable;
    @FXML private TableColumn<Factura, Integer> idColumn;
    @FXML private TableColumn<Factura, String> numeroFacturaColumn;
    @FXML private TableColumn<Factura, LocalDate> fechaColumn;
    @FXML private TableColumn<Factura, Double> montoColumn;
    @FXML private TableColumn<Factura, String> proveedorColumn;
    @FXML private TableColumn<Factura, String> estadoColumn;
    @FXML private Button btnregresarHome;
    @FXML private ComboBox<Proveedor> proveedorComboBox;

    private FacturaDAO facturaDAO;
    private ObservableList<Factura> facturasList;

    public void initialize() {
        facturaDAO = new FacturaDAO();
        facturasList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numeroFacturaColumn.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        montoColumn.setCellValueFactory(new PropertyValueFactory<>("monto"));
        proveedorColumn.setCellValueFactory(new PropertyValueFactory<>("proveedorId"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarFacturas();
        cargarProveedores();
    }

    private void cargarProveedores() {
        try {
            List<Proveedor> proveedores = facturaDAO.obtenerProveedores();
            if (proveedores == null || proveedores.isEmpty()) {
                mostrarAlerta("Advertencia", "No se encontraron proveedores disponibles.");
                return;
            }
            ObservableList<Proveedor> proveedoresList = FXCollections.observableArrayList(proveedores);
            proveedorComboBox.setItems(proveedoresList);
            proveedorComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Proveedor proveedor) {
                    return proveedor != null ? proveedor.getNombre() : "";
                }

                @Override
                public Proveedor fromString(String string) {
                    return proveedoresList.stream()
                            .filter(proveedor -> proveedor.getNombre().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los proveedores.");
        }
    }

    @FXML
    private void agregarFactura(ActionEvent event) {
        if (!validarCampos()) {
            mostrarAlerta("Campos incompletos", "Todos los campos son obligatorios.");
            return;
        }
        Proveedor proveedorSeleccionado = proveedorComboBox.getValue();
        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un proveedor.");
            return;
        }

        try {
            // Obtén los datos del formulario
            String numeroFactura = numeroFacturaField.getText();
            LocalDate fecha = fechaField.getValue();
            double monto;

            try {
                monto = Double.parseDouble(montoField.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta("Error en Monto", "El monto debe ser un número válido.");
                return;
            }


            String estado = "pendiente";



            // Crea la nueva factura
            Factura nuevaFactura = new Factura(0, numeroFactura, fecha, monto, proveedorSeleccionado.getId(), estado);
            facturaDAO.agregarFactura(nuevaFactura);

            cargarFacturas();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo agregar la factura.");
        }
    }

    private boolean validarCampos() {
        return !(numeroFacturaField.getText() == null || numeroFacturaField.getText().isEmpty() ||
                fechaField.getValue() == null ||
                montoField.getText() == null || montoField.getText().isEmpty());
    }

    private void limpiarCampos() {
        numeroFacturaField.clear();
        fechaField.setValue(null);
        montoField.clear();

    }


    private void cargarFacturas() {
        ObservableList<String> proveedores= FXCollections.observableArrayList();
        try {
            List<Factura> facturas = facturaDAO.listarFacturas("");
            facturasList.setAll(facturas); facturasTable.setItems(facturasList);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error",
                    "No se pudieron cargar las facturas.");
        }
    }

    @FXML
    public void obtenerProveedores() {
        try {
            cargarProveedores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarEstadoPagada() {
        Factura facturaSeleccionada = facturasTable.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada != null) {
            try {
                facturaDAO.actualizarEstado(facturaSeleccionada.getId(), "pagada");
                cargarFacturas();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cambiar el estado a 'pagada'.");
            }
        }
    }

    @FXML
    private void cambiarEstadoPendiente() {
        Factura facturaSeleccionada = facturasTable.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada != null) {
            try {
                facturaDAO.actualizarEstado(facturaSeleccionada.getId(), "pendiente");
                cargarFacturas();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cambiar el estado a 'pendiente'.");
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void volverHomeFact(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Stage stage = (Stage) btnregresarHome.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();
        }catch (IOException excep){
            excep.printStackTrace();
        }
    }
}