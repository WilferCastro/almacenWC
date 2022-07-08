package controllers;

import com.jfoenix.controls.JFXButton;
import data.metodos;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

public class InicioController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label usuario;
    @FXML
    private VBox sidebar;
    @FXML
    private JFXButton btn_Inicio;
    @FXML
    private JFXButton btn_ventas;
    @FXML
    private JFXButton btn_compras;
    @FXML
    private JFXButton btn_clientes;
    @FXML
    private JFXButton btn_proveedores;
    @FXML
    private JFXButton btn_informe;
    @FXML
    private JFXButton btn_productos;
    @FXML
    private JFXButton btn_perfil;
    @FXML
    private JFXButton btn_usuarios;
    @FXML
    private JFXButton btn_reportes;
    @FXML
    private JFXButton btn_config;
    @FXML
    private AnchorPane pPrincipal;
    @FXML
    private Label cargo;
    @FXML
    private PieChart grafica;
    @FXML
    private Label nombre;
    @FXML
    private ImageView logo;
    @FXML
    private Label slogan;
    @FXML
    private Label telefono;
    @FXML
    private Label nit;
    @FXML
    private Label lFecha;
    @FXML
    private Label lMes1;
    @FXML
    private Label lMes2;
    @FXML
    private Label lMes4;
    @FXML
    private Label lMes3;
    @FXML
    private Label empresaName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        start();
        grafica();
        btn_Inicio.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    conectar con = new conectar();
    Connection cn;

    private void select() {
        for (int i = 0; i < sidebar.getChildren().size(); i++) {
            sidebar.getChildren().get(i).setStyle("-fx-background-color: transparent; -fx-cursor: hand");
        }
    }

    private void grafica() {
        ObservableList list = FXCollections.observableArrayList();

        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT producto, COUNT(*) Total FROM detalle_venta GROUP BY producto HAVING COUNT(*) > 1");
            while (rs.next()) {
                list.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
            }
            grafica.setData(list);
            
            //RESULTADOS DE LAS VENTAS DE 4 MESES

            int mes=LocalDate.now().getMonthValue();
            
            rs = cn.createStatement().executeQuery("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%"+metodos.mesV2(mes)+"%'");
            while (rs.next()) {
                lMes1.setText("VENTAS "+metodos.mesV2(mes)+"\n"+rs.getString(1));
            }
            
            if (mes == 1) {
                mes = 13;
            }
            
            rs = cn.createStatement().executeQuery("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%"+metodos.mesV2(mes-1)+"%'");
            while (rs.next()) {
                lMes2.setText("VENTAS "+metodos.mesV2(mes-1)+"\n"+rs.getString(1));
            }
            
            rs = cn.createStatement().executeQuery("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%"+metodos.mesV2(mes-2)+"%'");
            while (rs.next()) {
                lMes3.setText("VENTAS "+metodos.mesV2(mes-2)+"\n"+rs.getString(1));
            }
            
            rs = cn.createStatement().executeQuery("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%"+metodos.mesV2(mes-3)+"%'");
            while (rs.next()) {
                lMes4.setText("VENTAS "+metodos.mesV2(mes-3)+"\n"+rs.getString(1));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
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

    private void start() {
        try {
            cn = con.conexion();
            usuario.setText(LoginController.usuario + "!");
            cargo.setText(LoginController.cargo);
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM accesos WHERE id_usuario=" + LoginController.id);
            while (rs.next()) {
                if (LoginController.cargo.equals("Usuario")) {
                    if (rs.getString("a_ventas").equals("NO")) {
                        sidebar.getChildren().remove(btn_ventas);
                    }
                    if (rs.getString("a_compras").equals("NO")) {
                        sidebar.getChildren().remove(btn_compras);
                    }
                    if (rs.getString("a_clientes").equals("NO")) {
                        sidebar.getChildren().remove(btn_clientes);
                    }
                    if (rs.getString("a_proveedores").equals("NO")) {
                        sidebar.getChildren().remove(btn_proveedores);
                    }
                    if (rs.getString("a_productos").equals("NO")) {
                        sidebar.getChildren().remove(btn_productos);
                    }
                    if (rs.getString("a_reportes").equals("NO")) {
                        sidebar.getChildren().remove(btn_informe);
                    }
                    if (rs.getString("a_perfil").equals("NO")) {
                        sidebar.getChildren().remove(btn_perfil);
                    }

                    sidebar.getChildren().remove(btn_reportes);
                    sidebar.getChildren().remove(btn_usuarios);
                    sidebar.getChildren().remove(btn_config);
                }

                rs = cn.createStatement().executeQuery("SELECT * FROM negocio");
                while (rs.next()) {
                    Blob blob = rs.getBlob("imagen");
                    Image image = SwingFXUtils.toFXImage(javax.imageio.ImageIO.read(blob.getBinaryStream()), null);
                    nombre.setText(rs.getString("nombre"));
                    empresaName.setText(rs.getString("nombre"));
                    logo.setImage(image);
                    nit.setText(rs.getString("nit"));
                    telefono.setText(rs.getString("telefono"));
                    slogan.setText(rs.getString("slogan"));
                }

                lFecha.setText(LocalDate.now().toString());
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion();
        }
    }

    @FXML
    private void inicio(ActionEvent event) {
        borderPane.setCenter(pPrincipal);
        select();
        btn_Inicio.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void usuarios(ActionEvent event) {
        centrar("/views/usuarios");
        select();
        btn_usuarios.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void productos(ActionEvent event) {
        centrar("/views/productos");
        select();
        btn_productos.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void clientes(ActionEvent event) {
        centrar("/views/clientes");
        select();
        btn_clientes.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void ventas(ActionEvent event) {
        centrar("/views/ventas");
        select();
        btn_ventas.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void compras(ActionEvent event) {
        centrar("/views/compras");
        select();
        btn_compras.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void proveedores(ActionEvent event) {
        centrar("/views/proveedores");
        select();
        btn_proveedores.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void informe(ActionEvent event) {
        centrar("/views/informe");
        select();
        btn_informe.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void reportes(ActionEvent event) {
        centrar("/views/reportes");
        select();
        btn_reportes.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void configuracion(ActionEvent event) {
        centrar("/views/configuracion");
        select();
        btn_config.setStyle("-fx-border-width: 0 5 0 0; -fx-border-color: #3f51b5;");
    }

    @FXML
    private void perfilEdit(ActionEvent event) {

        //CAMBIAR ESTO PARA EL MODO DE DESARROLLO
        PerfilController.id_usuario = LoginController.id;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/perfil.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Información De Usuario");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML
    private void cerrarSesion(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);

            stage.setScene(scene);
            stage.setTitle("Iniciar Sesión");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();

            Stage mystage = (Stage) this.borderPane.getScene().getWindow();
            mystage.close();

        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void finalizarApp(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void goFacebook(MouseEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://es-la.facebook.com/"));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void goWhatsapp(MouseEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://web.whatsapp.com/"));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void goInsta(MouseEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://www.instagram.com/?hl=es"));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
