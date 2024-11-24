package com.example.projecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconextion {
    public Connection databaselink;

    public Connection getConexion() {
        String usuarioDB = "root";
        String claveDB = "16150905";
        String url = "jdbc:mysql://localhost:3306/nextbyte";
        String driver = "com.mysql.cj.jdbc.Driver"; // Opcional, si necesitas cargar el driver explícitamente

        try {
            // Cargar el controlador JDBC (esto no es necesario si estás usando una versión reciente de JDBC)
            Class.forName(driver);

            // Establecer la conexión y asignarla a la variable de instancia
            databaselink = DriverManager.getConnection(url, usuarioDB, claveDB);
            System.out.println("Conexión exitosa");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }

        return databaselink;
    }
}

