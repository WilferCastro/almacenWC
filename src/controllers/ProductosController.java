package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.productos;
import data.proveedores;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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

public class ProductosController implements Initializable {

    @FXML
    private TableView<productos> tabla;
    @FXML
    private TableColumn<productos, Integer> id;
    @FXML
    private TableColumn<productos, String> codigo;
    @FXML
    private TableColumn<productos, String> producto;
    @FXML
    private TableColumn<productos, Integer> precioVenta;
    @FXML
    private TableColumn<productos, Integer> precioCompra;
    @FXML
    private TableColumn<productos, String> categoria;
    @FXML
    private TableColumn<productos, Integer> stock;
    @FXML
    private TableColumn<productos, String> descripcion;
    @FXML
    private JFXTextField buscador;
    @FXML
    private Label contador;
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
        load("SELECT * FROM productos p,categorias c WHERE p.idCategoria=c.id_categoria");
    }

    ObservableList<productos> lista;
    conectar con = new conectar();
    Connection cn;
    int index = -1;
    productos pro;
    DecimalFormat df=new DecimalFormat();
    
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
                int p1=rs.getInt("precio_venta"),p2=rs.getInt("precio_compra");
                String idCategoria=rs.getInt("id_categoria")+" - "+rs.getString("categoria");
                lista.add(new productos(rs.getInt("id_producto"),df.format(p1), df.format(p2), rs.getInt("stock"), rs.getString("codigo"), rs.getString("producto"), rs.getString("categoria"), rs.getString("descripcion"), idCategoria));
            }

            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            producto.setCellValueFactory(new PropertyValueFactory<>("producto"));
            precioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
            precioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
            categoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
            descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            stock.setCellValueFactory(new PropertyValueFactory<>("stock"));

            tabla.setItems(lista);
            contador.setText(lista.size() + "");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }

    }
    
    public static Stage stage;
    
    private void open(String metodo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/nuevoProducto.fxml"));
            Parent root = loader.load();

            NuevoProductoController n = loader.getController();
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
                load("SELECT * FROM productos p,categorias c WHERE p.idCategoria=c.id_categoria");
                botons();
            }

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
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

    @FXML
    private void buscarUsuario(KeyEvent event) {
        load("SELECT * FROM productos p,categorias c WHERE p.idCategoria=c.id_categoria && producto LIKE '%" + buscador.getText() + "%' ");
    }
    
    @FXML
    private void editar(ActionEvent event) {
        open("EDITAR", "Editar Producto");
    }

    @FXML
    private void nuevoProducto(ActionEvent event) {
        open("AGREGAR", "Agregar Nuevo Producto");
    }

    @FXML
    private void eliminar(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("CONFIRMAR.");
        a.setContentText("Desea eliminar el producto '" + producto.getCellData(index) + "' permanentemenete?.");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("DELETE FROM productos WHERE id_producto=" + id.getCellData(index));
                ps.executeUpdate();
                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El producto '" + producto.getCellData(index) + "' ha sido eliminado.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();
                load("SELECT * FROM productos p,categorias c WHERE p.idCategoria=c.id_categoria");
                botons();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    

    @FXML
    private void infoAdicional(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("PRODUCTO: " + producto.getCellData(index));
        alert.setTitle("Info");
        alert.setContentText("DESCRIPCION: \n\n" + descripcion.getCellData(index));
        alert.showAndWait();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        tabla.getSelectionModel().clearSelection();
        botons();
    }

    @FXML
    private void categorias(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/categorias.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);

            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }

}
