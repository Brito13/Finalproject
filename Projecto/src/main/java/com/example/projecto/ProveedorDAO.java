package com.example.projecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//proveedorDAO
public class ProveedorDAO {
    private Connection connection;

    public ProveedorDAO() {
        DBconextion db = new DBconextion();
        connection = db.getConexion();
    }

    // Agregar proveedor
    public void agregarProveedor(Proveedor proveedor) throws SQLException {
        String query = "INSERT INTO proveedores (nombre, contacto, direccion) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, proveedor.getNombre());
        ps.setString(2, proveedor.getContacto());
        ps.setString(3, proveedor.getDireccion());
        ps.executeUpdate();
    }

    // Listar proveedores
    public List<Proveedor> listarProveedores() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String query = "SELECT ID_proveedor, nombre, contacto, direccion FROM proveedores";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Proveedor proveedor = new Proveedor(
                    rs.getInt("ID_proveedor"),
                    rs.getString("nombre"),
                    rs.getString("contacto"),
                    rs.getString("direccion")
            );
            proveedores.add(proveedor);
        }

        return proveedores;
    }

    // Editar proveedor
    public void editarProveedor(Proveedor proveedor) throws SQLException {
        String query = "UPDATE proveedores SET nombre = ?, contacto = ?, direccion = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, proveedor.getNombre());
        ps.setString(2, proveedor.getContacto());
        ps.setString(3, proveedor.getDireccion());
        ps.setInt(4, proveedor.getId());
        ps.executeUpdate();
    }

    // Eliminar proveedor
    public void eliminarProveedor(int id) throws SQLException {
        String query = "DELETE FROM proveedores WHERE id_proveedores = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}