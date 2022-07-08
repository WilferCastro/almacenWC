package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class PerfilController implements Initializable {

    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXTextField usuario;
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
    private JFXPasswordField nuevaPass;
    @FXML
    private JFXPasswordField confirmPass;
    @FXML
    private Label contador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }

    conectar con = new conectar();
    Connection cn;

    public static int id_usuario = 0;
    String pass;

    public String line(String nombre) {
        if (nombre.equals("")) {
            return "";
        }
        char[] caracteres = nombre.toCharArray();

        caracteres[0] = Character.toUpperCase(caracteres[0]);

        for (int i = 0; i < nombre.length() - 2; i++) {
            if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ',') {
                caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
            }
        }
        return new String(caracteres);
    }

    //ENCRIPTAR CONTRASEÑA
    private static String bcrypt(String cadena) {
        return Base64.getEncoder().encodeToString(cadena.getBytes());
    }

    //DESENCRIPTAR CONTRASEÑA
    private static String dcrypt(String cadena) throws UnsupportedEncodingException {
        byte decode[] = Base64.getDecoder().decode(cadena.getBytes());
        return new String(decode, "utf-8");
    }

    public void load() {
        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM usuarios WHERE id_usuario=" + id_usuario);
            while (rs.next()) {
                label2.setText(rs.getString("usuario"));
                nombre.setText(rs.getString("nombre"));
                usuario.setText(rs.getString("usuario"));
                pass = dcrypt(rs.getString("contrasena"));
                documento.setText(rs.getLong("documento") + "");
                telefono.setText(rs.getLong("telefono") + "");
                residencia.setText(rs.getString("residencia"));
                correo.setText(rs.getString("correo"));
                adicional.setText(rs.getString("adicional"));
                contador.setText(adicional.getText().length()+"/300");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PerfilController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion();
        }
    }

    @FXML
    private void editPersonal(ActionEvent event) {
        if (telefono.getText().isEmpty() || documento.getText().isEmpty() || nombre.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Debe ingresar su nombre, número de telefono y número de documento para continuar.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.CENTER);
            n.show();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Confirme su contraseña para actualizar su información");
            dialog.setHeaderText(null);
            dialog.setContentText("Ingrese su contraseña para continuar.");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                if (result.get().equals(pass)) {
                    try {
                        cn = con.conexion();
                        PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET nombre=?,documento=?,residencia=?,telefono=?,correo=?,adicional=? WHERE id_usuario=?");
                        ps.setString(1, line(nombre.getText()));
                        ps.setString(2, documento.getText());
                        ps.setString(3, residencia.getText());
                        ps.setString(4, telefono.getText());
                        ps.setString(5, correo.getText());
                        ps.setString(6, adicional.getText());
                        ps.setInt(7, id_usuario);
                        ps.executeUpdate();

                        Notifications n = Notifications.create()
                                .title("Información")
                                .text("Información actualizada con exito.")
                                .graphic(new ImageView(new Image("/images/good.png")))
                                .hideAfter(Duration.seconds(3))
                                .position(Pos.CENTER);
                        n.show();

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
    private void tipadoUsername(KeyEvent event) {
        if (usuario.getText().length() > 18) {
            event.consume();
        }
    }

    @FXML
    private void editUsername(ActionEvent event) {
        if (usuario.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nombre de usuario.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Confirme su contraseña.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ingrese su contraseña para continuar.");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                if (result.get().equals(pass)) {
                    try {
                        cn = con.conexion();
                        PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET usuario=? WHERE id_usuario=?");
                        ps.setString(1, line(usuario.getText()));
                        ps.setInt(2, id_usuario);
                        ps.executeUpdate();

                        Notifications n = Notifications.create()
                                .title("Información")
                                .text("Nombre de usuario actualizado.")
                                .graphic(new ImageView(new Image("/images/good.png")))
                                .hideAfter(Duration.seconds(3))
                                .position(Pos.CENTER);
                        n.show();
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
    }

    @FXML
    private void editPassword(ActionEvent event) {
        if (confirmPass.getText().isEmpty() || nuevaPass.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese y confirme su nueva contraseña.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else if (!nuevaPass.getText().equals(confirmPass.getText())) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Las contraseñas no coinciden. Intente Nuevamente.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else if (nuevaPass.getText().length() < 4) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("La contraseña debe contener almenos 4 caracteres.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Confirme su antigua contraseña.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ingrese su antigua contraseña para continuar.");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                if (result.get().equals(pass)) {
                    try {
                        cn = con.conexion();
                        PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET contrasena=? WHERE id_usuario=?");
                        ps.setString(1, bcrypt(nuevaPass.getText()));
                        ps.setInt(2, id_usuario);
                        ps.executeUpdate();

                        Notifications n = Notifications.create()
                                .title("Información")
                                .text("Su contraseña ha sido actualizada.")
                                .graphic(new ImageView(new Image("/images/good.png")))
                                .hideAfter(Duration.seconds(3))
                                .position(Pos.CENTER);
                        n.show();

                        pass = nuevaPass.getText();
                        confirmPass.setText("");
                        nuevaPass.setText("");

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
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Stage mystage = (Stage) this.confirmPass.getScene().getWindow();
        mystage.close();
    }

    @FXML
    private void tipadoAdicional(KeyEvent event) {
        if(adicional.getText().length()>298){
            event.consume();
        }
        contador.setText(adicional.getText().length()+"/300");
    }

}
