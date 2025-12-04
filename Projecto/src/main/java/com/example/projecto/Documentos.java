package com.example.projecto;

import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class Documentos {

    public void mostrarAlertaExito(String ruta) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reporte generado");
        alert.setHeaderText("¡Operación exitosa!");
        alert.setContentText("El archivo Excel fue generado correctamente en:\n" + ruta);

        alert.showAndWait();
    }



    public static void generarReporteExcel() {
        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {
            Workbook workbook = new XSSFWorkbook();
            crearHoja(workbook, conn, "Películas", "SELECT * FROM pelicula");
            crearHoja(workbook, conn, "Clientes", "SELECT * FROM clientes");
            crearHoja(workbook, conn, "Usuarios", "SELECT * FROM usuarios");

            String ruta = "Reportes/Reporte_FilmMagic.xlsx";
            FileOutputStream out = new FileOutputStream(ruta);
            workbook.write(out);
            out.close();
            workbook.close();

            System.out.println("Reporte generado exitosamente en: " + ruta);


        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Error generando el reporte: " + e.getMessage());
        }
    }


    private static void crearHoja(Workbook workbook, Connection conn, String nombreHoja, String query)
            throws SQLException {

        Sheet sheet = workbook.createSheet(nombreHoja);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row headerRow = sheet.createRow(0);
        for (int i = 1; i <= columnas; i++) {
            Cell cell = headerRow.createCell(i - 1);
            cell.setCellValue(meta.getColumnName(i));
            cell.setCellStyle(headerStyle);
        }

        int fila = 1;
        while (rs.next()) {
            Row row = sheet.createRow(fila++);
            for (int i = 1; i <= columnas; i++) {
                row.createCell(i - 1).setCellValue(rs.getString(i));
            }
        }

        for (int i = 0; i < columnas; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

