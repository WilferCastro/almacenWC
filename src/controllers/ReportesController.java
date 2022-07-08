
package controllers;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

public class ReportesController implements Initializable {

    @FXML
    private JFXButton b_productos;
    @FXML
    private JFXButton b_ventas;
    @FXML
    private JFXButton b_clientes;
    @FXML
    private JFXButton b_compras;
    @FXML
    private JFXButton b_proveedores;
    @FXML
    private JFXButton b_usuarios;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton btnInicio;
    @FXML
    private JFXButton btnVentas;
    @FXML
    private JFXButton btnCompras;
    @FXML
    private JFXButton btnEstadisticas;
    @FXML
    private AnchorPane panel1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnInicio.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
        estadisticas("SELECT COUNT(producto) FROM productos", "Productos\n",b_productos);
        estadisticas("SELECT COUNT(id_venta) FROM ventas", "Ventas\n",b_ventas);
        estadisticas("SELECT COUNT(id_compra) FROM Compras", "Compras\n",b_compras);
        estadisticas("SELECT COUNT(cliente) FROM clientes", "Clientes\n",b_clientes);
        estadisticas("SELECT COUNT(proveedor) FROM proveedores", "Proveedores\n",b_proveedores);
        estadisticas("SELECT COUNT(usuario) FROM usuarios", "Usuarios\n",b_usuarios);
    }    
    
    conectar con = new conectar();
    Connection cn;
    DecimalFormat df = new DecimalFormat();
    
    private void estadisticas(String sql,String name,JFXButton b){
        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                b.setText(name+""+df.format(rs.getInt(1)));
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    private void centrar(String name) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(name + ".fxml"));
        } catch (IOException ex) {
            System.out.println("error " + ex.getMessage());
        }
        borderPane.setCenter(root);
    }
    
    private void open(String ruta,String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(scene);

            stage.showAndWait();


        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
    } 
    
    private void style(){
        btnInicio.setStyle("-fx-background-color: transparent; -fx-cursor: hand");
        btnCompras.setStyle("-fx-background-color: transparent; -fx-cursor: hand");
        btnEstadisticas.setStyle("-fx-background-color: transparent; -fx-cursor: hand");
        btnVentas.setStyle("-fx-background-color: transparent; -fx-cursor: hand");
    }

    @FXML
    private void loadProductos(ActionEvent event) {
        open("/views/productos.fxml", "Listado de productos");
    }

    @FXML
    private void loadVentas(ActionEvent event) {
        centrar("/views/ventas_report");
        style();
        btnVentas.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
        
    }

    @FXML
    private void loadClientes(ActionEvent event) {
        open("/views/clientes.fxml", "Listado de clientes");
    }

    @FXML
    private void loadCompras(ActionEvent event) {
        centrar("/views/compras_report");
        style();
        btnCompras.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void loadProveedores(ActionEvent event) {
        open("/views/proveedores.fxml", "Listado de proveedores");
    }

    @FXML
    private void loadUsuarios(ActionEvent event) {
        open("/views/usuarios.fxml", "Listado de usuarios");
    }

    @FXML
    private void centrarInicio(ActionEvent event) {
        borderPane.setCenter(panel1);
        style();
        btnInicio.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void centrarVentas(ActionEvent event) {
        centrar("/views/ventas_report");
        style();
        btnVentas.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void centrarCompras(ActionEvent event) {
        centrar("/views/compras_report");
        style();
        btnCompras.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void centrarEstadisticas(ActionEvent event) {
        centrar("/views/estadisticas");
        style();
        btnEstadisticas.setStyle("-fx-border-width: 0 0 5 0; -fx-border-color: #3f51b5;");
    }
    
}
