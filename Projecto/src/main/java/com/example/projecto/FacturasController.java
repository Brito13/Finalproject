package com.example.projecto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.time.LocalDate;

public class FacturasController{
    @FXML private TextField numeroFacturaField;
    @FXML private DatePicker fechaField;
    @FXML private TextField montoField;
    @FXML private ComboBox<Proveedor> proveedorComboBox;
    @FXML private TableView<Factura> facturasTable;
    @FXML private TableColumn<Factura, Integer> idColumn;
    @FXML private TableColumn<Factura, String> numeroFacturaColumn;
    @FXML private TableColumn<Factura, LocalDate> fechaColumn;
    @FXML private TableColumn<Factura, Double> montoColumn;
    @FXML private TableColumn<Factura, String> proveedorColumn;
    @FXML private TableColumn<Factura, String> estadoColumn;

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
    }

    private void cargarFacturas() {
        try {
            facturasList.setAll(facturaDAO.listarFacturas(""));
            facturasTable.setItems(facturasList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarFactura() {

        double monto = Double.parseDouble(montoField.getText());
        Proveedor proveedor = proveedorComboBox.getSelectionModel().getSelectedItem();
        java.sql.Date fecha = java.sql.Date.valueOf(fechaField.getValue());

        Factura factura = new Factura(
                0,
                numeroFacturaField.getText(),
                fecha.toLocalDate(),
                Double.parseDouble(montoField.getText()),
                proveedorComboBox.getSelectionModel().getSelectedItem().getId(),
                "pendiente"
        );
        try {
            facturaDAO.agregarFactura(factura);
            cargarFacturas();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarEstadoPagada() {

    }

    @FXML
    private void cambiarEstadoPendiente() {

    }
}
