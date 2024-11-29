package com.example.projecto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class ReportsController{
    @FXML
    private DatePicker datedesde;

    @FXML
    private DatePicker datehasta;

    @FXML
    private TextField txtnom;

    @FXML
    private TextField txttel;

    @FXML
    private TableColumn<Factura, Integer> idcuenta;

    @FXML
    private TableColumn<Factura, Integer> idprovcolumn;

    @FXML
    private TableColumn<Factura, Double> montocolumn;

    @FXML
    private TableColumn<Factura, String> estadocolumn;

    @FXML
    private TableColumn<Factura, Date> fechacolum;

    @FXML
    private TableView<Factura> tableresults;


    @FXML
    private Button btnvolver;


    public void reportView(ActionEvent ex) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(""));
        Parent root = loader.load();
        Stage stage = (Stage) ((Button) ex.getSource()).getScene().getWindow();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("C:\\Users\\John\\Finalproject\\Projecto\\src\\main\\resources\\com\\example\\projecto\\styles.css")).toExternalForm());
        stage.setScene(scene);

        stage.show();
    }

    @FXML
    public void initialize(){
         idcuenta.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
         idprovcolumn.setCellValueFactory(new PropertyValueFactory<>("proveedorId"));
         estadocolumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
         montocolumn.setCellValueFactory(new PropertyValueFactory<>("monto"));
         fechacolum.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }

    public void filtrarFacturas(ActionEvent event){
        String nombre = txtnom.getText().trim();
        String contacto = txttel.getText().trim();

        if(nombre.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, complete todos los campos.");
            alerta.showAndWait();
            return;
        }

        if(contacto.isEmpty()){
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, complete todos los campos.");
            alerta.showAndWait();
            return;
        }

        if (datedesde.getValue() == null || datehasta.getValue() == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un rango de fechas.");
            alerta.showAndWait();
            return;
        }

        DBconextion DB = new DBconextion();
        Connection conextionDB = DB.getConexion();

        ObservableList<Factura> facturaList = FXCollections.observableArrayList();

        try {
            String query =  "SELECT f.id, f.numero_factura, f.fecha, f.monto, f.estado, f.proveedor_id "
                    + "FROM facturascuentas f " + "JOIN proveedores p ON f.proveedor_id = p.ID_proveedor "
                    + "WHERE p.nombre = ? AND p.contacto = ? AND f.estado = 'pendiente' "
                    + "AND f.fecha BETWEEN ? AND ?";

            PreparedStatement stmt = conextionDB.prepareStatement(query);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.sql.Date sqlFechaDesde = java.sql.Date.valueOf(datedesde.getValue());
            java.sql.Date sqlFechaHasta = java.sql.Date.valueOf(datehasta.getValue());

            stmt.setString(1, nombre);
            stmt.setString(2, contacto);
            stmt.setDate(3, sqlFechaDesde);
            stmt.setDate(4, sqlFechaHasta);
            ResultSet results = stmt.executeQuery();

            while (results.next()) {
                int id = results.getInt("id");
                int proveedorid = results.getInt("proveedor_id");
                String numeroFactura = results.getString("numero_factura");
                Date fecha = results.getDate("fecha");
                double monto = results.getDouble("monto");
                String estado = results.getString("estado");
                if (estado == null) {
                    estado = "pendiente";  // Asignar un valor predeterminado si es nulo
                }

                Factura factura = new Factura(id, numeroFactura, fecha, monto, proveedorid, estado);
                facturaList.add(factura);
            }
            tableresults.setItems(facturaList);

        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert alerta = new Alert(Alert.AlertType.ERROR, "No se encontraron los datos en la DB.");
            alerta.showAndWait();

        } finally {
            try {
                if (conextionDB != null) {
                    conextionDB.close();
                }
            }catch (SQLException ex){
                ex.printStackTrace();
            }

        }
    }

    public void returnHome(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Stage stage = (Stage) btnvolver.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();

        }catch (IOException ex){
            ex.printStackTrace();

        }
    }
}

