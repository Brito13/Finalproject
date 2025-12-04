package com.example.projecto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RentaController implements Initializable {

    @FXML private ComboBox<String> cbCliente;
    @FXML private ComboBox<String> cbPelicula;
    @FXML private DatePicker dpFechaRenta;
    @FXML private TextField txtPrecio;
    @FXML private DatePicker dpFechaDevolucion;

    @FXML private TableView<ObservableList<String>> tablaRentas;
    private ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarClientes();
        cargarPeliculas();
        inicializarColumnas();
        cargarRentasPendientes();
    }

    // ===========================================================
    // CARGAR CLIENTES
    // ===========================================================
    private void cargarClientes() {
        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT id, nombre FROM clientes");

            while (rs.next()) {
                cbCliente.getItems().add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // CARGAR PELÍCULAS DISPONIBLES
    // ===========================================================
    private void cargarPeliculas() {
        cbPelicula.getItems().clear();
        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT id, titulo FROM pelicula WHERE disponibilidad = 1");

            while (rs.next()) {
                cbPelicula.getItems().add(rs.getInt("id") + " - " + rs.getString("titulo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // REGISTRAR RENTA
    // ===========================================================
    @FXML
    private void registrarRenta() {

        if (cbCliente.getValue() == null || cbPelicula.getValue() == null
                || dpFechaRenta.getValue() == null || txtPrecio.getText().isEmpty()) {

            mostrarAlerta("Error", "Debe completar todos los campos.");
            return;
        }

        int clienteId = Integer.parseInt(cbCliente.getValue().split(" - ")[0]);
        int peliculaId = Integer.parseInt(cbPelicula.getValue().split(" - ")[0]);

        String fechaRenta = dpFechaRenta.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fechaVencimiento = dpFechaRenta.getValue().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            String sql = """
                INSERT INTO renta (fecha_renta, fecha_vencimiento, precio, pelicula_id, cliente_id, penalidad)
                VALUES (?,?,?,?,?,0)
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fechaRenta);
            stmt.setString(2, fechaVencimiento);
            stmt.setDouble(3, Double.parseDouble(txtPrecio.getText()));
            stmt.setInt(4, peliculaId);
            stmt.setInt(5, clienteId);

            stmt.executeUpdate();

            conn.createStatement()
                    .executeUpdate("UPDATE Pelicula SET disponibilidad = 0 WHERE id = " + peliculaId);

            mostrarAlerta("Éxito", "Renta registrada.");
            cargarRentasPendientes();
            cargarPeliculas();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // REGISTRAR DEVOLUCIÓN + CALCULAR PENALIDAD
    // ===========================================================
    @FXML
    private void registrarDevolucion() {

        ObservableList<String> fila = tablaRentas.getSelectionModel().getSelectedItem();

        if (fila == null) {
            mostrarAlerta("Error", "Debe seleccionar una renta.");
            return;
        }

        if (dpFechaDevolucion.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar una fecha de devolución.");
            return;
        }

        int rentaId = Integer.parseInt(fila.get(0));
        int peliculaId = Integer.parseInt(fila.get(8)); // posición nueva

        String fechaDev = dpFechaDevolucion.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            // 1) Actualizar fecha devolución
            conn.createStatement().executeUpdate(
                    "UPDATE renta SET fecha_devolucion = '" + fechaDev + "' WHERE id = " + rentaId
            );

            // 2) Llamar procedimiento para penalidad
            CallableStatement cs = conn.prepareCall("{ CALL sp_actualizar_penalidad(?) }");
            cs.setInt(1, rentaId);
            cs.execute();

            // 3) Hacer película disponible
            conn.createStatement().executeUpdate(
                    "UPDATE Pelicula SET disponibilidad = 1 WHERE id = " + peliculaId
            );

            mostrarAlerta("Éxito", "Devolución registrada y penalidad calculada.");
            cargarRentasPendientes();
            cargarPeliculas();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // CARGAR RENTAS
    // ===========================================================
    private void cargarRentasPendientes() {
        lista.clear();
        tablaRentas.getItems().clear();

        DBconextion DB = new DBconextion();

        try (Connection conn = DB.getConexion()) {

            String sql = """
                SELECT r.id, c.nombre AS cliente, p.titulo AS pelicula,
                       r.fecha_renta, r.fecha_vencimiento,
                       r.precio, r.fecha_devolucion,
                       r.penalidad, p.id AS peliculaId
                FROM renta r
                JOIN clientes c ON r.cliente_id = c.id
                JOIN pelicula p ON r.pelicula_id = p.id
                ORDER BY r.id DESC
            """;

            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {

                ObservableList<String> fila = FXCollections.observableArrayList();

                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("cliente"));
                fila.add(rs.getString("pelicula"));
                fila.add(rs.getString("fecha_renta"));
                fila.add(rs.getString("fecha_vencimiento"));
                fila.add(rs.getString("precio"));
                fila.add(rs.getString("fecha_devolucion"));
                fila.add(String.valueOf(rs.getDouble("penalidad")));
                fila.add(String.valueOf(rs.getInt("peliculaId")));

                lista.add(fila);
            }

            tablaRentas.setItems(lista);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void inicializarColumnas() {

        String[] titulos = {
                "ID", "Cliente", "Película", "Fecha Renta", "Fecha Venc.", "Precio",
                "Fecha Devolución", "Penalidad"
        };

        for (int i = 0; i < titulos.length; i++) {
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(titulos[i]);
            final int colIndex = i;
            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().get(colIndex)
            ));
            tablaRentas.getColumns().add(col);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.show();
    }
}

