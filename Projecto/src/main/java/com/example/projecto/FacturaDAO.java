package com.example.projecto;


import javafx.fxml.FXML;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class FacturaDAO{
    private Connection connection;

    public FacturaDAO() {
        DBconextion db = new DBconextion();
        connection = db.getConexion();
    }

    // Agregar factura
    public void agregarFactura(Factura factura) throws SQLException {
        String query = "INSERT INTO facturas (numero_factura, fecha, monto, proveedor_id, estado) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, factura.getNumeroFactura());
        ps.setDate(2, Date.valueOf(factura.getFecha()));
        ps.setDouble(3, factura.getMonto());
        ps.setInt(4, factura.getProveedorId());
        ps.setString(5, factura.getEstado());
        ps.executeUpdate();
    }

    // Listar facturas
    public List<Factura> listarFacturas(String filtro) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String query = "SELECT id, numero_factura, fecha, monto, proveedor_id, estado FROM facturas WHERE estado LIKE ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, "%" + filtro + "%");
        ResultSet rs = ps.executeQuery();


        while (rs.next()) {
            Factura factura = new Factura(
                    rs.getInt("id"),
                    rs.getString("numero_factura"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getDouble("monto"),
                    rs.getInt("proveedor_id"),
                    rs.getString("estado")
            );
            facturas.add(factura);
        }

        return facturas;
    }

    // Cambiar estado de factura
    public void actualizarEstado(int id, String nuevoEstado) throws SQLException {
        String query = "UPDATE facturas SET estado = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nuevoEstado);
        ps.setInt(2, id);
        ps.executeUpdate();
    }

    // Obtener proveedores para el combobox
    public List<Proveedor> obtenerProveedores() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();

        if (connection == null || connection.isClosed()) {
            throw new SQLException("La conexión con la base de datos no está activa.");
        }

        String query = "SELECT id_proveedor, nombre, direccion, contacto FROM proveedores";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_proveedor");
                String nombre = rs.getString("nombre");
                String contacto = rs.getString("contacto");
                String direccion = rs.getString("direccion");

                if (nombre == null) nombre = "Sin nombre";
                if (contacto == null) contacto = "Sin contacto";
                if (direccion == null) direccion = "Sin dirección";

                Proveedor proveedor = new Proveedor(id, nombre, contacto, direccion);
                proveedores.add(proveedor);
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta de proveedores.");
            e.printStackTrace();
            throw e;
        }

        return proveedores;
    }

    public List<String> listarFacturas() throws SQLException {
        List<String> facturas = new ArrayList<>();
        String query = "SELECT numero_factura FROM facturas WHERE estado = 'pendiente' ";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String numeroFactura = rs.getString("numero_factura");
                facturas.add(numeroFactura);
            }
        }
        return facturas;
    }
}
