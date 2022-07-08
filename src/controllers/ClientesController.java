package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.clientes;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class ClientesController implements Initializable {

    @FXML
    private TableView<clientes> tabla;
    @FXML
    private TableColumn<clientes, Integer> idCliente;
    @FXML
    private TableColumn<clientes, String> cliente;
    @FXML
    private TableColumn<clientes, Long> documento;
    @FXML
    private TableColumn<clientes, String> residencia;
    @FXML
    private TableColumn<clientes, Long> telefono;
    @FXML
    private TableColumn<clientes, String> correo;
    @FXML
    private TableColumn<clientes, Date> fecha;
    @FXML
    private TableColumn<clientes, String> adicional;
    @FXML
    private JFXTextField buscador;
    @FXML
    private Label contador;
    @FXML
    private JFXButton btnEliminar;
    @FXML
    private JFXButton btnEditar;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnNuevo;
    @FXML
    private JFXButton btnAdicional;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load("SELECT * FROM clientes");
    }

    ObservableList<clientes> lista;
    conectar con = new conectar();
    Connection cn;
    clientes cli;

    private void load(String sql) {
        try {
            lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new clientes(rs.getInt("id_cliente"), rs.getString("cliente"), rs.getString("correo"), rs.getString("residencia"), rs.getString("adicional"), rs.getDate("fecha"), rs.getLong("documento"), rs.getLong("telefono")));
            }

            idCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
            cliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
            documento.setCellValueFactory(new PropertyValueFactory<>("documento"));
            telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            residencia.setCellValueFactory(new PropertyValueFactory<>("residencia"));
            correo.setCellValueFactory(new PropertyValueFactory<>("correo"));
            fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            adicional.setCellValueFactory(new PropertyValueFactory<>("adicional"));

            tabla.setItems(lista);
            contador.setText(lista.size() + "");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }

    }

    int index = -1, id_cliente = -1;

    @FXML
    private void getData(MouseEvent event) {
        index = tabla.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            cli = tabla.getSelectionModel().getSelectedItem();
            btnCancelar.setVisible(true);
            btnEditar.setVisible(true);
            btnEliminar.setVisible(true);
            btnAdicional.setVisible(true);
        }
    }

    @FXML
    private void buscarCliente(KeyEvent event) {
        load("SELECT * FROM clientes WHERE cliente LIKE '%" + buscador.getText() + "%' "
                + "OR documento LIKE '%" + buscador.getText() + "%' "
                + "OR adicional LIKE '%" + buscador.getText() + "%' "
                + "OR residencia LIKE '%" + buscador.getText() + "%' ");
    }

    @FXML
    private void eliminar(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("CONFIRMAR.");
        a.setContentText("Desea eliminar al cliente '" + cliente.getCellData(index) + "' permanentemenete?.");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("DELETE FROM clientes WHERE id_cliente=" + idCliente.getCellData(index));
                ps.executeUpdate();
                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El cliente '" + cliente.getCellData(index) + "' ha sido eliminado.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();
                load("SELECT * FROM clientes");
                tabla.getSelectionModel().clearSelection();
                btnCancelar.setVisible(false);
                btnEditar.setVisible(false);
                btnEliminar.setVisible(false);
                btnAdicional.setVisible(false);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }

        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        tabla.getSelectionModel().clearSelection();
        btnCancelar.setVisible(false);
        btnEditar.setVisible(false);
        btnEliminar.setVisible(false);
        btnAdicional.setVisible(false);
    }
    
    public static Stage stage;

    private void open(String metodo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/nuevoCliente.fxml"));
            Parent root = loader.load();

            NuevoClienteController n = loader.getController();
            n.start(lista, cli, metodo);

            Scene scene = new Scene(root);
            stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);

            stage.setScene(scene);

            stage.showAndWait();

            int v = n.getValor();

            if (v == 1) {
                load("SELECT * FROM clientes");

                btnCancelar.setVisible(false);
                btnEditar.setVisible(false);
                btnEliminar.setVisible(false);
                btnAdicional.setVisible(false);
            }

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }

    @FXML
    private void editar(ActionEvent event) {
        open("EDITAR", "Editar Cliente");
    }

    @FXML
    private void addCliente(ActionEvent event) {
        open("AGREGAR", "Registrar Nuevo Cliente");
    }

    @FXML
    private void infoAdicional(ActionEvent event) {
        String da = fecha.getCellData(index).toString();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Información Adicional del cliente: " + cliente.getCellData(index));
        alert.setTitle("Info");
        alert.setContentText("CORREO ELECTRONICO:  " + correo.getCellData(index) + "\n\nFECHA DE REGISTRO:  " + da + "\n\nINFORMACION ADICIONAL: \n\n" + adicional.getCellData(index));
        alert.showAndWait();
    }


}
