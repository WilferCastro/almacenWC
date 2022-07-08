package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.metodos;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class LoginController implements Initializable {

    @FXML
    private JFXPasswordField pass;
    @FXML
    private JFXTextField user;
    @FXML
    private ImageView logo;
    @FXML
    private Label nombre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rec();
    }
    
    private void rec(){
        try {
            cn = con.conexion();
            rs = cn.createStatement().executeQuery("SELECT nombre,imagen FROM negocio");
            while (rs.next()) {
                Blob blob = rs.getBlob("imagen");
                Image image = SwingFXUtils.toFXImage(javax.imageio.ImageIO.read(blob.getBinaryStream()), null);
                nombre.setText(rs.getString("nombre"));
                logo.setImage(image);
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.cerrarConexion();
        }
    }

    conectar con = new conectar();
    Connection cn;
    ResultSet rs;
    public static int id;
    public static String usuario, cargo,contrasena;

    private void load() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/inicio.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            //stage.setResizable(false);
            //stage.resizableProperty().setValue(false);

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("PANEL DE INICIO");
            stage.show();

            Stage mystage = (Stage) this.user.getScene().getWindow();
            mystage.close();

        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

    private void acc() {
        if (user.getText().isEmpty() || pass.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese Usuario Y Contraseña")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {
            try {
                byte r;
                cn = con.conexion();
                String us=user.getText().replaceAll(".*([';]+|(--)+).*","");
                rs = cn.createStatement().executeQuery("SELECT id_usuario,usuario,cargo,contrasena FROM usuarios WHERE usuario='" + us + "' AND contrasena='" + metodos.bcrypt(pass.getText()) + "'");
                if (rs.next()) {
                    id = rs.getInt("id_usuario");
                    usuario = rs.getString("usuario");
                    cargo = rs.getString("cargo");
                    contrasena=rs.getString("contrasena");
                    r = 1;
                    if (r == 1) {
                        load();
                    }

                } else {
                    Notifications n = Notifications.create()
                            .title("Información")
                            .text("La información ingresada es incorrecta")
                            .graphic(new ImageView(new Image("/images/info.png")))
                            .hideAfter(Duration.seconds(3))
                            .position(Pos.CENTER);
                    n.show();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }

        }
    }

    @FXML
    private void ingresar(ActionEvent event) {
        acc();
    }

    @FXML
    private void accederEnter(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
            acc();
        }
    }

    @FXML
    private void changePass(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
            pass.requestFocus();
        }
    }

    @FXML
    private void finalizar(MouseEvent event) {
        System.exit(0);
    }

}
