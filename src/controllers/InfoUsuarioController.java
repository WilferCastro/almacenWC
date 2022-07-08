package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.metodos;
import java.io.UnsupportedEncodingException;
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
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class InfoUsuarioController implements Initializable {

    @FXML
    private Tab tab3;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXTextField usuario;
    @FXML
    private JFXComboBox<String> cargo;
    @FXML
    private JFXTextField nombre;
    @FXML
    private Label label2;
    @FXML
    private JFXTextField documento;
    @FXML
    private JFXTextField residencia;
    @FXML
    private JFXTextField correo;
    @FXML
    private JFXTextField telefono;
    @FXML
    private JFXTextArea adicional;
    @FXML
    private Label label3;
    @FXML
    private JFXCheckBox a_ventas;
    @FXML
    private JFXCheckBox a_compras;
    @FXML
    private JFXCheckBox a_proveedores;
    @FXML
    private JFXCheckBox a_clientes;
    @FXML
    private JFXCheckBox a_reportes;
    @FXML
    private JFXCheckBox a_productos;
    @FXML
    private JFXCheckBox a_perfil;
    @FXML
    private Label lVentas;
    @FXML
    private Label lProveedores;
    @FXML
    private Label lProductos;
    @FXML
    private Label lReportes;
    @FXML
    private Label lCompras;
    @FXML
    private Label lClientes;
    @FXML
    private Label lPerfil;
    @FXML
    private Label contador;
    @FXML
    private JFXPasswordField nuevaPass;
    @FXML
    private JFXPasswordField confirmPass;
    @FXML
    private JFXButton btn1;
    @FXML
    private JFXButton btn2;
    @FXML
    private JFXButton btn3;
    @FXML
    private Label fecha;
    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargo.setItems(combo);
        if(id_usuario==1){
            btn1.setVisible(false);
            btn2.setVisible(false);
            btn3.setVisible(false);
        }
        load();
    }

    ObservableList combo = FXCollections.observableArrayList("Usuario", "Administrador");
    conectar con = new conectar();
    Connection cn;
    public static int id_usuario = 0;
    int valor=0;

    public int getValor() {
        return valor;
    }

    public void load() {
        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM usuarios,accesos WHERE usuarios.id_usuario=accesos.id_usuario AND usuarios.id_usuario=" + id_usuario);
            while (rs.next()) {
                nombre.setText(rs.getString("nombre"));
                usuario.setText(rs.getString("usuario"));
                if (rs.getString("cargo").equals("Administrador")) {
                    tab3.setDisable(true);
                }
                cargo.setValue(rs.getString("cargo"));
                documento.setText(rs.getLong("documento") + "");
                telefono.setText(rs.getLong("telefono") + "");
                residencia.setText(rs.getString("residencia"));
                correo.setText(rs.getString("correo"));
                adicional.setText(rs.getString("adicional"));
                fecha.setText(rs.getString("fecha"));
                label2.setText(rs.getString("usuario"));
                label3.setText(rs.getString("usuario"));
                contador.setText(adicional.getText().length() + "/300");
                if (rs.getString("a_ventas").equals("SI")) {
                    lVentas.setText("SI");
                    a_ventas.setSelected(true);
                }
                if (rs.getString("a_compras").equals("SI")) {
                    lCompras.setText("SI");
                    a_compras.setSelected(true);
                }
                if (rs.getString("a_clientes").equals("SI")) {
                    lClientes.setText("SI");
                    a_clientes.setSelected(true);
                }
                if (rs.getString("a_proveedores").equals("SI")) {
                    lProveedores.setText("SI");
                    a_proveedores.setSelected(true);
                }
                if (rs.getString("a_productos").equals("SI")) {
                    lProductos.setText("SI");
                    a_productos.setSelected(true);
                }
                if (rs.getString("a_reportes").equals("SI")) {
                    lReportes.setText("SI");
                    a_reportes.setSelected(true);
                }
                if (rs.getString("a_perfil").equals("SI")) {
                    lPerfil.setText("SI");
                    a_perfil.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    @FXML
    private void a_ventas(ActionEvent event) {
        if (a_ventas.isSelected()) {
            lVentas.setText("SI");
        } else {
            lVentas.setText("NO");
        }
    }

    @FXML
    private void a_compras(ActionEvent event) {
        if (a_compras.isSelected()) {
            lCompras.setText("SI");
        } else {
            lCompras.setText("NO");
        }
    }

    @FXML
    private void a_proveedores(ActionEvent event) {
        if (a_proveedores.isSelected()) {
            lProveedores.setText("SI");
        } else {
            lProveedores.setText("NO");
        }
    }

    @FXML
    private void a_clientes(ActionEvent event) {
        if (a_clientes.isSelected()) {
            lClientes.setText("SI");
        } else {
            lClientes.setText("NO");
        }
    }

    @FXML
    private void a_reportes(ActionEvent event) {
        if (a_reportes.isSelected()) {
            lReportes.setText("SI");
        } else {
            lReportes.setText("NO");
        }
    }

    @FXML
    private void a_productos(ActionEvent event) {
        if (a_productos.isSelected()) {
            lProductos.setText("SI");
        } else {
            lProductos.setText("NO");
        }
    }

    @FXML
    private void a_perfil(ActionEvent event) {
        if (a_perfil.isSelected()) {
            lPerfil.setText("SI");
        } else {
            lPerfil.setText("NO");
        }
    }
    
    private void validar(String sql,String mensaje) throws UnsupportedEncodingException {
        TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ingrese su contraseña para continuar.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ingrese su contraseña de administrador para continuar.");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                String pass=metodos.dcrypt(LoginController.contrasena);
                if (result.get().equals(pass)) {
                    try {
                        cn = con.conexion();
                        //String replace = sql.replaceAll(".*([';]+|(--)+).*","");
                        PreparedStatement ps = cn.prepareStatement(sql);
                        ps.executeUpdate();

                        Notifications n = Notifications.create()
                                .title("Información")
                                .text(mensaje)
                                .graphic(new ImageView(new Image("/images/good.png")))
                                .hideAfter(Duration.seconds(3))
                                .position(Pos.CENTER);
                        n.show();
                        valor=1;

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                        Logger.getLogger(PerfilController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        con.cerrarConexion();
                    }
                } else {
                    Notifications n = Notifications.create()
                            .title("Información")
                            .text("La contraseña ingresada es incorrecta, intente nuevamente.")
                            .graphic(new ImageView(new Image("/images/info.png")))
                            .hideAfter(Duration.seconds(3))
                            .position(Pos.CENTER);
                    n.show();
                }
            }
    }

    @FXML
    private void actualizarUsuario(ActionEvent event) {
        if (usuario.getText().isEmpty() || cargo.getValue() == null) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nuevo nombre de usuario y su cargo.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            try {
                String sql="UPDATE usuarios SET usuario='"+metodos.line(usuario.getText())+"', cargo='"+cargo.getValue()+"' WHERE id_usuario="+id_usuario;
                validar(sql,"Nombre de usuario y cargo actualizado");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(InfoUsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    private void actualizarContrasena(ActionEvent event) {
        if (nuevaPass.getText().isEmpty() || confirmPass.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese y confirme la nueva contraseña.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        }else if(!nuevaPass.getText().equals(confirmPass.getText())){
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Las contraseñas no coinciden, intente nuevamente.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        }else if(nuevaPass.getText().length()<4){
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("La contraseña debe contener almenos 4 caracteres.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        }else{
            try {
                String sql="UPDATE usuarios SET contrasena='"+metodos.bcrypt(nuevaPass.getText())+"' WHERE id_usuario="+id_usuario;
                validar(sql, "La contraseña del usuario '"+label2.getText()+"' ha sido modificada.");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(InfoUsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void actualizarPersonal(ActionEvent event) {
        if (telefono.getText().isEmpty() || documento.getText().isEmpty() || nombre.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Debe ingresar su nombre, número de telefono y número de documento para continuar.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.CENTER);
            n.show();
        } else {
            try {
                String sql="UPDATE usuarios SET nombre='"+metodos.line(nombre.getText())+"',"
                    + "documento='"+documento.getText()+"',"
                    + "residencia='"+residencia.getText()+"',"
                    + "telefono='"+telefono.getText()+"',"
                    + "correo='"+correo.getText()+"',"
                    + "adicional='"+adicional.getText()+"'"
                    + "WHERE id_usuario="+id_usuario;
                validar(sql,"La información del usuario '"+label2.getText()+"' ha sido actualizada.");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(InfoUsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void actualizarAccesos(ActionEvent event) {
        try {
            String sql="UPDATE accesos SET a_ventas='"+lVentas.getText()+"',"
                    + "a_compras='"+lCompras.getText()+"',"
                    + "a_clientes='"+lClientes.getText()+"',"
                    + "a_proveedores='"+lProveedores.getText()+"',"
                    + "a_productos='"+lProductos.getText()+"',"
                    + "a_reportes='"+lReportes.getText()+"',"
                    + "a_perfil='"+lPerfil.getText()+"'"
                    + "WHERE id_usuario="+id_usuario;
            validar(sql, "Los accesos del usuario '"+label2.getText()+"' han sido modificados.");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(InfoUsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void tipadoDocumento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '0' || c > '9') {
            event.consume();
        } else if (documento.getText().length() > 9) {
            event.consume();
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

    @FXML
    private void selectCargo(ActionEvent event) {
        if (cargo.getValue().equals("Administrador")) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("INFORMACION.");
            a.setContentText("El tipo de usuario 'ADMINISTRADOR' tiene acceso a todas las funciones del sistema.\nProceda con precaución.");
            a.setHeaderText(null);
            a.showAndWait();
        }
        
    }

    @FXML
    private void permisos(ActionEvent event) {
        tabPane.getSelectionModel().select(tab3);
    }

    @FXML
    private void InformacionPersonal(ActionEvent event) {
        tabPane.getSelectionModel().select(tab2);
    }

    @FXML
    private void inicioSesion(ActionEvent event) {
        tabPane.getSelectionModel().select(tab1);
    }

}
