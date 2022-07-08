package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import data.categorias;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class CategoriasController implements Initializable {

    @FXML
    private TableView<categorias> tabla;
    @FXML
    private TableColumn<categorias, Integer> id;
    @FXML
    private TableColumn<categorias, String> categoria;
    @FXML
    private JFXTextField t_categoria;
    @FXML
    private JFXCheckBox c1;
    @FXML
    private JFXCheckBox c2;
    @FXML
    private JFXTextField buscador;
    @FXML
    private JFXButton btnEliminar;
    @FXML
    private JFXButton btnAgregar;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnActualizar;
    @FXML
    private AnchorPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load("SELECT * FROM categorias");
        translate();
    }

    conectar con = new conectar();
    Connection cn;
    int index = -1;
    double xof=0,yof=0;
    
    private void translate(){
        parent.setOnMousePressed((event) -> {
            xof=event.getSceneX();
            yof=event.getSceneY();
        });
        
        parent.setOnMouseDragged((event) -> {
            ProductosController.stage.setX(event.getScreenX() - xof );
            ProductosController.stage.setY(event.getScreenY() - yof );
        });
    }

    private void load(String sql) {
        try {
            ObservableList lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new categorias(rs.getInt("id_categoria"), rs.getString("categoria")));
            }

            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            categoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

            tabla.setItems(lista);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    @FXML
    private void agregarCategoria(ActionEvent event) {
        if (t_categoria.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nombre de la nueva categoria")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("INSERT INTO categorias(categoria) VALUES(?)");
                if (c1.isSelected()) {
                    ps.setString(1, t_categoria.getText().toUpperCase());
                } else {
                    ps.setString(1, t_categoria.getText());
                }
                ps.executeUpdate();

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("La categoria '" + t_categoria.getText() + "' ha sido agregada a la lista.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();

                load("SELECT * FROM categorias");
                t_categoria.setText("");

            } catch (SQLException ex) {
                Logger.getLogger(CategoriasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private void clean(){
        t_categoria.setText("");
        btnActualizar.setVisible(false);
        btnCancelar.setVisible(false);
        btnEliminar.setVisible(false);
        
        btnAgregar.setVisible(true);
        index=-1;
    }

    @FXML
    private void cancelar(ActionEvent event) {
        tabla.getSelectionModel().clearSelection();
        
        clean();
    }

    @FXML
    private void actualizarCategoria(ActionEvent event) {
        if (t_categoria.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nombre de la categoria")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("UPDATE categorias SET categoria=? WHERE id_categoria=?");
                if (c2.isSelected()) {
                    ps.setString(1, t_categoria.getText().toUpperCase());
                } else {
                    ps.setString(1, t_categoria.getText());
                }
                ps.setInt(2, id.getCellData(index));
                ps.executeUpdate();

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("La categoria '" + categoria.getCellData(index) + "' ha sido actualizada.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();

                load("SELECT * FROM categorias");
                clean();

            } catch (SQLException ex) {
                Logger.getLogger(CategoriasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void buscarCategoria(KeyEvent event) {
        load("SELECT * FROM categorias WHERE categoria LIKE '%" + buscador.getText() + "%' ");
    }

    @FXML
    private void eliminarCategoria(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("CONFIRMAR.");
        a.setContentText("Todos los productos pertenecientes a esta categoria también serán eliminados, desea continuar?");
        a.setHeaderText("Desea eliminar la categoria '" + categoria.getCellData(index) + "' permanentemenete?.");
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("DELETE FROM categorias WHERE id_categoria=" + id.getCellData(index));
                ps.executeUpdate();
                Notifications n = Notifications.create()
                        .title("Información")
                        .text("La categoria '" + categoria.getCellData(index) + "' ha sido eliminada.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();

                load("SELECT * FROM categorias");
                clean();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    @FXML
    private void cerrarModal(MouseEvent event) {
        Stage mystage = (Stage) this.t_categoria.getScene().getWindow();
        mystage.close();
    }

    @FXML
    private void getIndex(MouseEvent event) {
        index = tabla.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            if (id.getCellData(index)!=1) {
                t_categoria.setText(categoria.getCellData(index));
                btnCancelar.setVisible(true);
                btnEliminar.setVisible(true);
                btnActualizar.setVisible(true);
                btnAgregar.setVisible(false);
            }else{
                clean();
            }
        }
    }


}
