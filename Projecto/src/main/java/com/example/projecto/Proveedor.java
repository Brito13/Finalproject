package com.example.projecto;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.*;

import java.net.IDN;

public class Proveedor {
    private SimpleIntegerProperty id;
    private SimpleStringProperty nombre;
    private SimpleStringProperty contacto;
    private SimpleStringProperty direccion;

    // Constructor
    public Proveedor(int id, String nombre, String contacto, String direccion) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.contacto = new SimpleStringProperty(contacto);
        this.direccion = new SimpleStringProperty(direccion);
    }

    // Getters y Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getContacto() {
        return contacto.get();
    }

    public void setContacto(String contacto) {
        this.contacto.set(contacto);
    }

    public StringProperty contactoProperty() {
        return contacto;
    }

    public String getDireccion() {
        return direccion.get();
    }

    public void setDireccion(String direccion) {
        this.direccion.set(direccion);
    }

    public StringProperty direccionProperty() {
        return direccion;
    }

    @Override
    public String toString() {
        return getNombre();
    }
}


