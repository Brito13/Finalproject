package com.example.projecto;

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
}

