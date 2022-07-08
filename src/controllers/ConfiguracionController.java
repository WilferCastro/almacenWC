package controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class ConfiguracionController implements Initializable {

    @FXML
    private JFXToggleButton tog1;
    @FXML
    private JFXToggleButton tog2;
    @FXML
    private JFXToggleButton tog3;
    @FXML
    private Label label1;
    @FXML
    private JFXToggleButton tog4;
    @FXML
    private Label label2;
    @FXML
    private JFXToggleButton tog5;
    @FXML
    private JFXToggleButton tog6;
    @FXML
    private JFXTextField nombre;
    @FXML
    private JFXTextField slogan;
    @FXML
    private ImageView logo;
    @FXML
    private Label ruta;
    @FXML
    private JFXTextField nit;
    @FXML
    private JFXTextField telefono;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }

    conectar con = new conectar();
    Connection cn;

    private void load() {
        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM opciones WHERE id_opcion=1");
            while (rs.next()) {
                if (rs.getInt("autoVenta") == 1) {
                    tog1.setSelected(true);
                }
                if (rs.getInt("autoCompra") == 1) {
                    tog2.setSelected(true);
                }
                if (rs.getInt("stockVenta") > 0) {
                    tog3.setSelected(true);
                    label1.setText("Los productos con un stock menor a " + rs.getInt("stockVenta") + " no se mostrarán");
                    label1.setVisible(true);
                }
                if (rs.getInt("stockCompra") < 100000) {
                    tog4.setSelected(true);
                    label2.setText("Los productos con un stock mayor a " + rs.getInt("stockCompra") + " no se mostrarán");
                    label2.setVisible(true);
                }
                if (rs.getInt("alertVenta") == 1) {
                    tog5.setSelected(true);
                }
                if (rs.getInt("alertCompra") == 1) {
                    tog6.setSelected(true);
                }
            }

            rs = cn.createStatement().executeQuery("SELECT * FROM negocio");
            while (rs.next()) {
                Blob blob = rs.getBlob("imagen");
                Image image = SwingFXUtils.toFXImage(javax.imageio.ImageIO.read(blob.getBinaryStream()), null);
                nombre.setText(rs.getString("nombre"));
                slogan.setText(rs.getString("slogan"));
                logo.setImage(image);
                nit.setText(rs.getString("nit"));
                telefono.setText(rs.getString("telefono"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NuevoProductoController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfiguracionController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion();
        }
    }

    private void update(String sql, int valor) {
        try {
            cn = con.conexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, valor);
            ps.executeUpdate();
            System.out.println("todo bien manito XD");
        } catch (SQLException ex) {
            Logger.getLogger(ConfiguracionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void decrementoStock(ActionEvent event) {
        //TOG 1 --> REDUCIR STOCK AL REALIZAR UNA VENTA
        if (tog1.isSelected()) {
            update("UPDATE opciones SET autoVenta=? WHERE id_opcion=1", 1);
        } else {
            update("UPDATE opciones SET autoVenta=? WHERE id_opcion=1", 0);
        }
    }

    @FXML
    private void aumentoStock(ActionEvent event) {
        //TOG 2 --> AUMENTAR STOCK AL REALIZAR UNA COMPRA
        if (tog2.isSelected()) {
            update("UPDATE opciones SET autoCompra=? WHERE id_opcion=1", 1);
        } else {
            update("UPDATE opciones SET autoCompra=? WHERE id_opcion=1", 0);
        }
    }

    @FXML
    private void stockMinimo(ActionEvent event) {
        //TOG 3 --> OCULTA PRODUCTOS EN PANEL DE VENTAS
        if (tog3.isSelected()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ingrese el nuevo valor.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocultar productos con stock menor a:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                try {
                    int aux = Integer.parseInt(result.get());

                    if (aux > 100000 || aux < 0) {
                        JOptionPane.showMessageDialog(null, "Ingrese un valor entre 0 y 100000");
                        tog3.setSelected(false);
                    } else {
                        update("UPDATE opciones SET stockVenta=? WHERE id_opcion=1", aux);
                        label1.setText("Los productos con un stock menor a " + aux + " no se mostrarán");
                        label1.setVisible(true);
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "El valor ingresado es incorrecto.\n\n" + e.getMessage());
                    tog3.setSelected(false);
                }
            } else {
                tog3.setSelected(false);
            }
        } else {
            label1.setVisible(false);
            update("UPDATE opciones SET stockVenta=? WHERE id_opcion=1", 0);
        }
    }

    @FXML
    private void stockMaximo(ActionEvent event) {
        //TOG 4 --> OCULTA PRODUCTOS EN PANEL DE COMPRAS
        if (tog4.isSelected()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ingrese el nuevo valor.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocultar productos con stock mayor a:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                try {
                    int aux = Integer.parseInt(result.get());

                    if (aux > 100000 || aux < 0) {
                        JOptionPane.showMessageDialog(null, "Ingrese un valor entre 0 y 100000");
                        tog4.setSelected(false);
                    } else {
                        update("UPDATE opciones SET stockCompra=? WHERE id_opcion=1", aux);
                        label2.setText("Los productos con un stock mayor a " + aux + " no se mostrarán");
                        label2.setVisible(true);
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "El valor ingresado es incorrecto.\n\n" + e.getMessage());
                    tog4.setSelected(false);
                }
            } else {
                tog4.setSelected(false);
            }
        } else {
            label2.setVisible(false);
            update("UPDATE opciones SET stockCompra=? WHERE id_opcion=1", 100000);
        }
    }

    @FXML
    private void alertVenta(ActionEvent event) {
        //TOG 5 --> MOSTRAR ALERTA DE CONFIRMACION EN PANEL DE VENTAS
        if (tog5.isSelected()) {
            update("UPDATE opciones SET alertVenta=? WHERE id_opcion=1", 1);
        } else {
            update("UPDATE opciones SET alertVenta=? WHERE id_opcion=1", 0);
        }
    }

    @FXML
    private void alertCompra(ActionEvent event) {
        //TOG 6 --> MOSTRAR ALERTA DE CONFIRMACION EN PANEL DE COMPRAS
        if (tog6.isSelected()) {
            update("UPDATE opciones SET alertCompra=? WHERE id_opcion=1", 1);
        } else {
            update("UPDATE opciones SET alertCompra=? WHERE id_opcion=1", 0);
        }
    }

    @FXML
    private void RestablecerVentas(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar.");
        a.setContentText("Desea restablecer las opciones del panel de ventas?");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            update("UPDATE opciones SET autoVenta=? WHERE id_opcion=1", 1);
            tog1.setSelected(true);
            update("UPDATE opciones SET stockVenta=? WHERE id_opcion=1", 5);
            tog3.setSelected(true);
            label1.setText("Los productos con un stock menor a 5 no se mostrarán");
            label1.setVisible(true);
            update("UPDATE opciones SET alertVenta=? WHERE id_opcion=1", 1);
            tog5.setSelected(true);

            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Las opciones de ventas han sido restablecidas.")
                    .graphic(new ImageView(new Image("/images/good.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.TOP_CENTER);
            n.show();
        }
    }

    @FXML
    private void RestablecerCompras(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar.");
        a.setContentText("Desea restablecer las opciones del panel de compras?");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            update("UPDATE opciones SET autoCompra=? WHERE id_opcion=1", 1);
            tog2.setSelected(true);
            update("UPDATE opciones SET stockCompra=? WHERE id_opcion=1", 10);
            label2.setText("Los productos con un stock mayor a 10 no se mostrarán");
            tog4.setSelected(true);
            label2.setVisible(true);
            update("UPDATE opciones SET alertCompra=? WHERE id_opcion=1", 1);
            tog6.setSelected(true);

            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Las opciones de compras han sido restablecidas.")
                    .graphic(new ImageView(new Image("/images/good.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.TOP_CENTER);
            n.show();
        }
    }

    @FXML
    private void alertVentas(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Información");
        alert.setContentText("Desactiva esta opcion si quieres que todos los productos registrados se muestren en el panel de ventas.");
        alert.showAndWait();
    }

    @FXML
    private void alertCompras(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Información");
        alert.setContentText("Desactiva esta opcion si quieres que todos los productos\nregistrados se muestren en el panel de compras.");
        alert.showAndWait();
    }

    @FXML
    private void selectLogo(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Elige el logo de tu empresa o negocio.");

        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File archivo = chooser.showOpenDialog(new Stage());
        if (archivo != null) {
            System.out.println(archivo.getAbsolutePath());
            ruta.setText(archivo.getAbsolutePath());
            Image image = new Image(archivo.toURI().toString());
            logo.setImage(image);
        }
    }

    @FXML
    private void actualizarInfo(ActionEvent event) {
        if (nombre.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nombre de su empresa o negocio.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.TOP_CENTER);
            n.show();
        } else {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("UPDATE negocio SET nombre=?, slogan=?, nit=?, telefono=? WHERE id_opcion=1");
                ps.setString(1, nombre.getText());
                ps.setString(2, slogan.getText());
                ps.setString(3, nit.getText());
                ps.setString(4, telefono.getText());
                ps.executeUpdate();

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("Información actualizada.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_CENTER);
                n.show();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Imagen no soportada,Seleccione otra imagen.\n" + ex.getMessage());
                Logger.getLogger(ConfiguracionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void actualizarLogo(ActionEvent event) {
        if (ruta.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Seleccione el nuevo logo.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.TOP_CENTER);
            n.show();
        } else {
            FileInputStream archivo;
            try {
                archivo = new FileInputStream(ruta.getText());
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("UPDATE negocio SET imagen=? WHERE id_opcion=1");
                ps.setBinaryStream(1, archivo);
                ps.executeUpdate();

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("Logo actualizado.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_CENTER);
                n.show();
            } catch (FileNotFoundException | SQLException ex) {
                Logger.getLogger(ConfiguracionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void tipadoTelefono(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '0' || c > '9') {
            event.consume();
        } else if (telefono.getText().length() > 9) {
            event.consume();
        }
    }
    
    

}
