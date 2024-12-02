package com.example.projecto;
import javafx.beans.property.*;
import java.sql.Date;
import java.time.LocalDate;

public class Factura {
    private IntegerProperty id;
    private StringProperty numeroFactura;
    private ObjectProperty<LocalDate> fecha;
    private DoubleProperty monto;
    private IntegerProperty proveedorId;
    private StringProperty estado;

    // Constructor
    public Factura(int id, String numeroFactura, LocalDate fecha, double monto, int proveedorId, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.numeroFactura = new SimpleStringProperty(numeroFactura);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.monto = new SimpleDoubleProperty(monto);
        this.proveedorId = new SimpleIntegerProperty(proveedorId);
        this.estado = new SimpleStringProperty(estado);
    }

    // Getters y Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNumeroFactura() {
        return numeroFactura.get();
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura.set(numeroFactura);
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public void setFecha(LocalDate fecha) {
        this.fecha.set(fecha);
    }

    public double getMonto() {
        return monto.get();
    }

    public void setMonto(double monto) {
        this.monto.set(monto);
    }

    public int getProveedorId() {
        return proveedorId.get();
    }

    public void setProveedorId(int proveedorId) {
        this.proveedorId.set(proveedorId);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    // Métodos de propiedad para usar en PropertyValueFactory
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty numeroFacturaProperty() {
        return numeroFactura;
    }

    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    public DoubleProperty montoProperty() {
        return monto;
    }

    public IntegerProperty proveedorIdProperty() {
        return proveedorId;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
