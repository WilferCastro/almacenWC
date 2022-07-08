package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.usuarios;
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
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class UsuariosController implements Initializable {

    @FXML
    private TableView<usuarios> tabla;
    @FXML
    private TableColumn<usuarios, Integer> idUser;
    @FXML
    private TableColumn<usuarios, String> nombre;
    @FXML
    private TableColumn<usuarios, String> usuario;
    @FXML
    private TableColumn<usuarios, String> cargo;
    @FXML
    private TableColumn<usuarios, Long> documento;
    @FXML
    private TableColumn<usuarios, String> residencia;
    @FXML
    private TableColumn<usuarios, Long> telefono;
    @FXML
    private Label contador;
    @FXML
    private JFXButton btnEliminar;
    @FXML
    private JFXButton btnAdicional;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXTextField buscador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load("SELECT id_usuario,documento,nombre,usuario,cargo,residencia,telefono FROM usuarios");
    }

    ObservableList<usuarios> lista;
    conectar con = new conectar();
    Connection cn;

    int index = -1, id_usuario = -1;

    private void load(String sql) {
        try {
            lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new usuarios(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("usuario"), rs.getString("cargo"), rs.getLong("documento"), rs.getString("residencia"), rs.getLong("telefono")));
            }

            idUser.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            usuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
            cargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
            documento.setCellValueFactory(new PropertyValueFactory<>("documento"));
            residencia.setCellValueFactory(new PropertyValueFactory<>("residencia"));
            telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

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
            if (idUser.getCellData(index) == 1) {
                btnAdicional.setVisible(true);
                btnEliminar.setVisible(false);
                btnCancelar.setVisible(true);
            } else {
                btnAdicional.setVisible(true);
                btnEliminar.setVisible(true);
                btnCancelar.setVisible(true);
            }
            id_usuario = idUser.getCellData(index);
        }
    }

    @FXML
    private void eliminar(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("CONFIRMAR.");
        a.setContentText("Desea eliminar al usuario '" + usuario.getCellData(index) + "' permanentemente?.");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("DELETE FROM usuarios WHERE id_usuario=" + idUser.getCellData(index));
                ps.executeUpdate();
                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El usuario '" + usuario.getCellData(index) + "' ha sido eliminado.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();
                load("SELECT id_usuario,documento,nombre,usuario,cargo,residencia,telefono FROM usuarios");
                can();

            } catch (SQLException e) {
                System.out.println("error " + e.getMessage());
            }

        }
    }
    
    private void open(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta+".fxml"));
            Parent root = loader.load();

            NuevoUsuarioController n = loader.getController();
            int aux=idUser.getCellData(lista.size()-1);
            n.start(lista,aux);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(scene);

            stage.showAndWait();

            int v = n.getValor();

            if (v == 1) {
                load("SELECT id_usuario,documento,nombre,usuario,cargo,residencia,telefono FROM usuarios");
                can();
            }

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }

    @FXML
    private void infoAdicional(ActionEvent event) {
        InfoUsuarioController.id_usuario = id_usuario;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/infoUsuario.fxml"));
            Parent root = loader.load();

            InfoUsuarioController i = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Información de usuario");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(scene);

            stage.showAndWait();

            int v = i.getValor();

            if (v == 1) {
                load("SELECT id_usuario,documento,nombre,usuario,cargo,residencia,telefono FROM usuarios");
                can();
            }

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }
    
    void can(){
        btnCancelar.setVisible(false);
        btnEliminar.setVisible(false);
        btnAdicional.setVisible(false);
        index = -1;
        id_usuario=-1;
    }

    @FXML
    private void cancelar(ActionEvent event) {
        tabla.getSelectionModel().clearSelection();
        can();
    }

    @FXML
    private void nuevoUsuario(ActionEvent event) {
        open("/views/nuevoUsuario", "Registrar Nuevo Usuario");
    }

    @FXML
    private void buscarUsuario(KeyEvent event) {
        load("SELECT id_usuario,documento,nombre,usuario,cargo,residencia,telefono FROM usuarios WHERE usuario LIKE '%" + buscador.getText() + "%' "
                + "OR nombre LIKE '%" + buscador.getText() + "%' "
                + "OR documento LIKE '%" + buscador.getText() + "%' "
                + "OR telefono LIKE '%" + buscador.getText() + "%' "
                + "OR residencia LIKE '%" + buscador.getText() + "%' ");
    }

}
