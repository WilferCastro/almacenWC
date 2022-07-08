package controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.clientes;
import data.metodos;
import data.usuarios;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class NuevoUsuarioController implements Initializable {

    @FXML
    private JFXTabPane tabPane;
    @FXML
    private Tab tab1;
    @FXML
    private JFXTextField usuario;
    @FXML
    private JFXComboBox<String> cargo;
    @FXML
    private JFXPasswordField nuevaPass;
    @FXML
    private JFXPasswordField confirmPass;
    @FXML
    private Tab tab2;
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
    private Label contador;
    @FXML
    private Tab tab3;
    @FXML
    private JFXCheckBox a_ventas;
    @FXML
    private Label label3;
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
    private Pane panePermisos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargo.setItems(combo);
        cargo.setValue("Usuario");
        System.out.println(adicional.getText().length());
    }

    ObservableList combo = FXCollections.observableArrayList("Usuario", "Administrador");
    ObservableList<usuarios> lista;
    conectar con = new conectar();
    Connection cn;
    int valor = 0, idUsuario, aux;

    public void start(ObservableList<usuarios> lista, int aux) {
        System.out.println("vamos a agregar");
        this.lista = lista;
        this.aux = aux;
    }

    public int getValor() {
        return valor;
    }

    @FXML
    private void selectCargo(ActionEvent event) {
        if (cargo.getValue().equals("Administrador")) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("INFORMACION.");
            a.setContentText("Este permiso puede ser modificado posteriormente.\nProceda con precaución.");
            a.setHeaderText("El tipo de usuario 'ADMINISTRADOR' tiene acceso a todas las funciones del sistema.");
            a.showAndWait();
            panePermisos.setDisable(true);

            a_clientes.setSelected(true);
            a_proveedores.setSelected(true);
            a_ventas.setSelected(true);
            a_compras.setSelected(true);
            a_perfil.setSelected(true);
            a_productos.setSelected(true);
            a_reportes.setSelected(true);

            lClientes.setText("SI");
            lCompras.setText("SI");
            lPerfil.setText("SI");
            lProductos.setText("SI");
            lProveedores.setText("SI");
            lReportes.setText("SI");
            lVentas.setText("SI");

        } else {
            panePermisos.setDisable(false);
        }

    }

    @FXML
    private void RegistrarUsuario(ActionEvent event) {
        if (usuario.getText().isEmpty() || nuevaPass.getText().isEmpty() || confirmPass.getText().isEmpty() || cargo.getValue() == null) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Los campos de texto con asteriscos no pueden quedar vacios.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else if (!nuevaPass.getText().equals(confirmPass.getText())) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Las contraseñas no coinciden, intente nuevamente.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {

            if (!documento.getText().isEmpty()) {
                for (int i = 0; i < lista.size(); i++) {
                    if (Long.parseLong(documento.getText()) == lista.get(i).getDocumento()) {
                        Notifications n = Notifications.create()
                                .title("Información")
                                .text("El número de documento ingresado ya se encuentra registrado.")
                                .graphic(new ImageView(new Image("/images/info.png")))
                                .hideAfter(Duration.seconds(3))
                                .position(Pos.CENTER);
                        n.show();
                        return;
                    }
                }
            }

            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("INSERT INTO usuarios(id_usuario,nombre,usuario,contrasena,cargo,documento,telefono,residencia,correo,adicional,fecha) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
                ps.setInt(1, aux + 1);
                ps.setString(2, metodos.line(metodos.line(nombre.getText())));
                ps.setString(3, metodos.line(metodos.line(usuario.getText())));
                ps.setString(4, metodos.bcrypt(nuevaPass.getText()));
                ps.setString(5, cargo.getValue());
                if (documento.getText().isEmpty()) {
                    ps.setString(6, null);
                } else {
                    ps.setString(6, documento.getText());
                }
                if (telefono.getText().isEmpty()) {
                    ps.setString(7, null);
                } else {
                    ps.setString(7, telefono.getText());
                }
                ps.setString(8, residencia.getText());
                ps.setString(9, correo.getText());
                ps.setString(10, adicional.getText());
                ps.setString(11, LocalDate.now().toString());

                ps.executeUpdate();

                ps = cn.prepareStatement("INSERT INTO accesos(id_usuario,a_ventas,a_compras,a_clientes,a_proveedores,a_reportes,a_productos,a_perfil) VALUES(?,?,?,?,?,?,?,?)");
                ps.setInt(1, aux + 1);
                ps.setString(2, lVentas.getText());
                ps.setString(3, lCompras.getText());
                ps.setString(4, lClientes.getText());
                ps.setString(5, lProveedores.getText());
                ps.setString(6, lReportes.getText());
                ps.setString(7, lProductos.getText());
                ps.setString(8, lPerfil.getText());

                ps.executeUpdate();
                valor = 1;
                aux++;

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El usuario " + usuario.getText() + " ha sido agregado a la lista.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_CENTER);
                n.show();

                usuario.setText("");
                nombre.setText("");
                documento.setText("");
                nuevaPass.setText("");
                confirmPass.setText("");
                cargo.setValue("Usuario");
                residencia.setText("");
                telefono.setText("");
                correo.setText("");
                adicional.setText("Tipo de Documento:\nEdad:\nGenero:\nFecha de Nacimiento:\nTelefono Alternativo:\nNOTA:");
                contador.setText("81/300");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }
        }
    }

    @FXML
    private void InformacionPersonal(ActionEvent event) {
        tabPane.getSelectionModel().select(tab2);
    }

    @FXML
    private void permisos(ActionEvent event) {
        tabPane.getSelectionModel().select(tab3);
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
    private void tipadoAdicional(KeyEvent event) {
        if (adicional.getText().length() > 299) {
            event.consume();
        }
        contador.setText(adicional.getText().length() + "/300");
    }

    @FXML
    private void inicioSesion(ActionEvent event) {
        tabPane.getSelectionModel().select(tab1);
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

    @FXML
    private void porDefecto(ActionEvent event) {
        a_clientes.setSelected(true);
        a_proveedores.setSelected(true);
        a_ventas.setSelected(true);
        a_compras.setSelected(true);
        a_perfil.setSelected(true);
        a_productos.setSelected(false);
        a_reportes.setSelected(true);

        lClientes.setText("SI");
        lCompras.setText("SI");
        lPerfil.setText("SI");
        lProductos.setText("NO");
        lProveedores.setText("SI");
        lReportes.setText("SI");
        lVentas.setText("SI");
    }

    

}
