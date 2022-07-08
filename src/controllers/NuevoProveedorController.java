
package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.metodos;
import data.proveedores;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;


public class NuevoProveedorController implements Initializable {

    @FXML
    private Label titulo;
    @FXML
    private JFXTextField proveedor;
    @FXML
    private JFXTextField telefono;
    @FXML
    private JFXTextField residencia;
    @FXML
    private JFXTextField correo;
    @FXML
    private JFXTextArea adicional;
    @FXML
    private JFXButton btnAgregar;
    @FXML
    private JFXButton btnActualizar;
    @FXML
    private Label contador;
    @FXML
    private AnchorPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translate();
    }    
    
    ObservableList<proveedores> lista;
    conectar con = new conectar();
    Connection cn;
    int valor = 0, idProveedor;
    double xof=0,yof=0;
    
    private void translate(){
        parent.setOnMousePressed((event) -> {
            xof=event.getSceneX();
            yof=event.getSceneY();
        });
        
        parent.setOnMouseDragged((event) -> {
            ProveedoresController.stage.setX(event.getScreenX() - xof );
            ProveedoresController.stage.setY(event.getScreenY() - yof );
        });
    }
    
    public void start(ObservableList<proveedores> lista, proveedores p, String in) {
        if (in.equals("EDITAR")) {
            titulo.setText("Editar Proveedor");
            btnActualizar.setVisible(true);
            btnAgregar.setVisible(false);

            idProveedor = p.getId();

            proveedor.setText(p.getProveedor());
            telefono.setText(p.getTelefono() + "");
            residencia.setText(p.getResidencia());
            correo.setText(p.getCorreo());
            adicional.setText(p.getAdicional());
            contador.setText(adicional.getText().length() + "/300");
            
        } else {
            this.lista = lista;
        }

    }
    
    public int getValor() {
        return valor;
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

    @FXML
    private void tipadoAdicional(KeyEvent event) {
        if (adicional.getText().length() > 299) {
            event.consume(); 
        }
        contador.setText(adicional.getText().length() + "/300");
    }

    @FXML
    private void addProveedor(ActionEvent event) {
        if (proveedor.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("El nombre o razón social del proveedor es obligatorio")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("INSERT INTO proveedores(proveedor,telefono,direccion,correo,adicional) VALUES (?,?,?,?,?)");
                ps.setString(1, metodos.line(proveedor.getText()));
                if (telefono.getText().isEmpty()) {
                    ps.setString(2, null);
                } else {
                    ps.setString(2, telefono.getText());
                }
                ps.setString(3, residencia.getText());
                ps.setString(4, correo.getText());
                ps.setString(5, adicional.getText());
                ps.executeUpdate();
                valor = 1;

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El proveedor " + proveedor.getText() + " ha sido agregado a la lista.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_CENTER);
                n.show();

                proveedor.setText("");
                residencia.setText("");
                telefono.setText("");
                correo.setText("");
                adicional.setText("Fax:\nPágina Web:\nTeléfono Alternativo:\nDesripción:");
                contador.setText("51/300");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }
        }
    }

    @FXML
    private void editProveedor(ActionEvent event) {
        if (proveedor.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("El nombre o razón social del proveedor es obligatorio")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {

            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("UPDATE proveedores SET proveedor=?,telefono=?,direccion=?,correo=?,adicional=? WHERE id_proveedor=?");
                ps.setString(1, metodos.line(proveedor.getText()));
                if (telefono.getText().isEmpty()) {
                    ps.setString(2, null);
                } else {
                    ps.setString(2, telefono.getText());
                }
                ps.setString(3, residencia.getText());
                ps.setString(4, correo.getText());
                ps.setString(5, adicional.getText());
                ps.setInt(6, idProveedor);
                ps.executeUpdate();
                valor = 1;

                Stage mystage = (Stage) this.proveedor.getScene().getWindow();
                mystage.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    @FXML
    private void cerraModal(MouseEvent event) {
        Stage mystage = (Stage) this.proveedor.getScene().getWindow();
        mystage.close();
    }
    
}
