package com.example.projecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    private Connection connection;

    public ProveedorDAO() {
        DBconextion db = new DBconextion();
        connection = db.getConexion();
    }

    // Agregar proveedor
    public void agregarProveedor(Proveedor proveedor) throws SQLException {
        if (proveedor.getNombre() == null || proveedor.getContacto() == null || proveedor.getDireccion() == null) {
            throw new IllegalArgumentException("Los campos de nombre, contacto y dirección no pueden ser nulos.");
        }

        String query = "INSERT INTO proveedores (nombre, contacto, direccion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getContacto());
            ps.setString(3, proveedor.getDireccion());
            ps.executeUpdate();
        }
    }

    public boolean proveedorExistente(String nombre, String contacto) throws SQLException {
        String query = "SELECT COUNT(*) FROM proveedores WHERE nombre = ? OR contacto = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, contacto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Listar proveedores
    public List<Proveedor> listarProveedores() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String query = "SELECT ID_proveedor, nombre, contacto, direccion FROM proveedores";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor(
                        rs.getInt("ID_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("contacto"),
                        rs.getString("direccion")
                );
                proveedores.add(proveedor);
            }
        }
        return proveedores;
    }

    // Editar proveedor
    public void editarProveedor(Proveedor proveedor) throws SQLException {
        if (proveedor.getId() <= 0 || proveedor.getNombre() == null || proveedor.getContacto() == null || proveedor.getDireccion() == null) {
            throw new IllegalArgumentException("Los campos de ID, nombre, contacto y dirección no pueden ser nulos.");
        }

        String query = "UPDATE proveedores SET nombre = ?, contacto = ?, direccion = ? WHERE ID_proveedor = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getContacto());
            ps.setString(3, proveedor.getDireccion());
            ps.setInt(4, proveedor.getId());
            ps.executeUpdate();
        }
    }

    public boolean facturasAsociadas(int proveedorId) throws SQLException {
        String query = "SELECT COUNT(*) FROM facturas WHERE proveedor_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, proveedorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Eliminar proveedor
    public void eliminarProveedor(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID del proveedor no puede ser menor o igual a 0.");
        }

        String query = "DELETE FROM proveedores WHERE ID_proveedor = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String> listarNombres() throws SQLException {
        List<String> proveedores = new ArrayList<>();
        String query = "SELECT nombre FROM proveedores";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                proveedores.add(nombre);
            }
        }
        return proveedores;
    }

    public List<String> listarProvpendientes() throws SQLException {
        List<String> proveedoresPendientes = new ArrayList<>();
        //consulta para obtener los proveedores de facturas pendientes
        String query = "SELECT p.nombre FROM facturas f JOIN proveedores p ON f.proveedor_id = p.ID_proveedor WHERE f.estado = 'pendiente'";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String nombreProveedor = rs.getString("nombre");
                proveedoresPendientes.add(nombreProveedor);
            }
        }
        return proveedoresPendientes;
    }
}
