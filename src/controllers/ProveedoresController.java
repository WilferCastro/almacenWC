package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.proveedores;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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

public class ProveedoresController implements Initializable {

    @FXML
    private TableView<proveedores> tabla;
    @FXML
    private TableColumn<proveedores, Integer> idProveedor;
    @FXML
    private TableColumn<proveedores, String> proveedor;
    @FXML
    private TableColumn<proveedores, String> telefono;
    @FXML
    private TableColumn<proveedores, String> residencia;
    @FXML
    private TableColumn<proveedores, String> correo;
    @FXML
    private TableColumn<proveedores, String> adicional;
    @FXML
    private Label contador;
    @FXML
    private JFXTextField buscador;
    @FXML
    private JFXButton btnEliminar;
    @FXML
    private JFXButton btnEditar;
    @FXML
    private JFXButton btnAdicional;
    @FXML
    private JFXButton btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load("SELECT * FROM proveedores");
    }

    ObservableList<proveedores> lista;
    conectar con = new conectar();
    Connection cn;
    int index = -1;
    proveedores pro;

    private void botons() {
        btnCancelar.setVisible(false);
        btnEditar.setVisible(false);
        btnEliminar.setVisible(false);
        btnAdicional.setVisible(false);
        index = -1;
    }

    private void load(String sql) {
        try {
            lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new proveedores(rs.getInt("id_proveedor"), rs.getString("proveedor"), rs.getLong("telefono"), rs.getString("direccion"), rs.getString("correo"), rs.getString("adicional")));
            }

            idProveedor.setCellValueFactory(new PropertyValueFactory<>("id"));
            proveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
            telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            residencia.setCellValueFactory(new PropertyValueFactory<>("residencia"));
            correo.setCellValueFactory(new PropertyValueFactory<>("correo"));
            adicional.setCellValueFactory(new PropertyValueFactory<>("adicional"));

            tabla.setItems(lista);
            contador.setText(lista.size() + "");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }

    }

    @FXML
    private void getData(MouseEvent event) {
        index = tabla.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            pro = tabla.getSelectionModel().getSelectedItem();
            btnCancelar.setVisible(true);
            btnEditar.setVisible(true);
            btnEliminar.setVisible(true);
            btnAdicional.setVisible(true);
        }
    }
    
    public static Stage stage;

    private void open(String metodo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/nuevoProveedor.fxml"));
            Parent root = loader.load();

            NuevoProveedorController n = loader.getController();
            n.start(lista, pro, metodo);
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
                load("SELECT * FROM proveedores");
                botons();
                System.out.println("SE EJECUTO LA SENTENCIA");
            }

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
    } 

    @FXML
    private void eliminar(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("CONFIRMAR.");
        a.setContentText("Desea eliminar al proveedor '" + proveedor.getCellData(index) + "' permanentemenete?.");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("DELETE FROM proveedores WHERE id_proveedor=" + idProveedor.getCellData(index));
                ps.executeUpdate();
                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El proveedor '" + proveedor.getCellData(index) + "' ha sido eliminado.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();
                load("SELECT * FROM proveedores");
                botons();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    @FXML
    private void editar(ActionEvent event) {
         open("EDITAR", "Editar Proveedor");
    }
    
    @FXML
    private void nuevoProveedor(ActionEvent event) {
         open("AGREGAR", "Registrar Nuevo Proveedor");
    }

    @FXML
    private void infoAdicional(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Información Adicional del proveedor: " + proveedor.getCellData(index));
        alert.setTitle("Info");
        alert.setContentText("INFORMACION ADICIONAL: \n\n" + adicional.getCellData(index));
        alert.showAndWait();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        tabla.getSelectionModel().clearSelection();
        botons();
    }

    @FXML
    private void buscarProveedor(KeyEvent event) {
        load("SELECT * FROM proveedores WHERE proveedor LIKE '%" + buscador.getText() + "%' "
                + "OR telefono LIKE '%" + buscador.getText() + "%' "
                + "OR correo LIKE '%" + buscador.getText() + "%' "
                + "OR direccion LIKE '%" + buscador.getText() + "%' ");
    }

}
