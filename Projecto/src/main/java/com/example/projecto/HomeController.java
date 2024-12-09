package com.example.projecto;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class HomeController extends Application {


    @FXML Label timeLabel;  // El Label donde se mostrará la hora
    @FXML Label dateLabel;  // El Label donde se mostrará el día
    @FXML private Button bttnconection; // Boton para acceder vista reportes John
    private ProveedorDAO proveedorDAO;
    private FacturaDAO facturaDAO;
    @FXML private Button bttnconectionProv;
    @FXML private Button bttnagregarfactura;
    @FXML private TableView<String> Pendientes;
    @FXML private TableView<String> Proveedores;
    @FXML private TableView<String> facturas_pendientes;
    @FXML private TableColumn<String,String > colnumero;
    @FXML private TableColumn<String, String> colnombreUsuarios;
    @FXML private TableColumn<String, String> columnombre;
    @FXML private Label label_facturas; //para mostrar el total de facturas
    @FXML private Label label_pagadas;  //para mostrar el total de facturas pagas
    @FXML private Label label_usuarios; //para mostrar el total de usuarios
    private Connection connection;
    public HomeController() {
        // Inicializamos
        this.proveedorDAO = new ProveedorDAO();
        this.facturaDAO = new FacturaDAO();
    }
    // Metodo para inicializar la conexión a la base de datos
    private void initializeConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/clientes";
        String user = "root";
        String password = "john310502";
        connection = DriverManager.getConnection(url, user, password);
    }

    public void initialize() {
        try {
            initializeConnection();
            loadLabelText();
            columnombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            cargarProveedores();
            colnumero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            cargarFacturas();
            colnombreUsuarios.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            cargarProveedoresPendientes();
            cargarContadores();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadLabelText() {
        // Obtener la hora actual
        LocalTime time = LocalTime.now();
        LocalDate today = LocalDate.now();

        // Definir el formato para el día de la semana
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");

        // Formatear la fecha para obtener el nombre completo del día
        String dayOfWeek = today.format(formatter);


        // Definir el formato de la hora
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");  // Puedes personalizar el formato
        String formattedTime = time.format(timeFormatter);  // Formatear la hora en el formato deseado

        // Establecer el texto del label con la hora actual
        timeLabel.setText(formattedTime);
        dateLabel.setText(dayOfWeek);
    }

    @FXML
    private void handleSwitchView(ActionEvent event) throws IOException {
        try{
            HBox secondaryView = FXMLLoader.load(getClass().getResource("Reports.fxml"));

            Scene secondaryScene = new Scene(secondaryView);

            Stage stage = (Stage) bttnconection.getScene().getWindow();

            stage.setScene(secondaryScene);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void registrarProveView(ActionEvent event) throws IOException{
        try{
            VBox thirdView = FXMLLoader.load(getClass().getResource("Proveedores.fxml"));

            Scene thirdScene = new Scene(thirdView);

            Stage stage = (Stage) bttnconectionProv.getScene().getWindow();

            stage.setScene(thirdScene);

        }catch (IOException e){
            e.printStackTrace();

        }
    }

    @FXML
    private void registrarFacturaView(ActionEvent event) throws IOException{
        try{
            VBox fourthView = FXMLLoader.load(getClass().getResource("Facturas.fxml"));

            Scene fourthScene = new Scene(fourthView);

            Stage stage = (Stage) bttnagregarfactura.getScene().getWindow();

            stage.setScene(fourthScene);

        }catch (IOException e){
            e.printStackTrace();

        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

    }

    public void cargarProveedores() {
        try {
            if (proveedorDAO == null) {
                throw new IllegalStateException("ProveedorDAO no está inicializado.");
            }

            ObservableList<String> proveedoresList = FXCollections.observableArrayList(proveedorDAO.listarNombres());
            Proveedores.setItems(proveedoresList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cargarFacturas() {
        try {
            if (facturaDAO == null) {
                throw new IllegalStateException("FacturaDAO no está inicializado.");
            }
            ObservableList<String> facturasList = FXCollections.observableArrayList(facturaDAO.listarFacturas());
            facturas_pendientes.setItems(facturasList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cargarProveedoresPendientes(){
        try {
            if(proveedorDAO == null) {
                throw new IllegalStateException("ProveedorDAO no está inicializado.");
            }

            ObservableList<String> proveedoresPendientes = FXCollections.observableArrayList(proveedorDAO.listarProvpendientes());
            Pendientes.setItems(proveedoresPendientes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo para cargar los contadores en los labels
    private void cargarContadores() throws SQLException {
        // Contar usuarios
        int usuariosCount = contarUsuarios();
        label_usuarios.setText(String.valueOf(usuariosCount));

        // Contar facturas totales
        int facturasTotalesCount = contarFacturasTotales();
        label_facturas.setText(String.valueOf(facturasTotalesCount));

        // Contar facturas pagadas
        int facturasPagadasCount = contarFacturasPagadas();
        label_pagadas.setText(String.valueOf(facturasPagadasCount));
    }

    // Metodo para contar usuarios
    private int contarUsuarios() throws SQLException {
        String query = "SELECT COUNT(*) FROM proveedores";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);  // Retorna el conteo de usuarios
            }
        }
        return 0;
    }

    // Metodo para contar todas las facturas
    private int contarFacturasTotales() throws SQLException {
        String query = "SELECT COUNT(*) FROM facturas";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Metodo para contar facturas pagadas
    private int contarFacturasPagadas() throws SQLException {
        String query = "SELECT COUNT(*) FROM facturas WHERE estado = 'pagada'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}