package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.clientes;
import data.metodos;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
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

public class NuevoClienteController implements Initializable {

    @FXML
    private JFXTextField cliente;
    @FXML
    private JFXTextField documento;
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
    private JFXDatePicker fecha;
    @FXML
    private Label contador;
    @FXML
    private Label titulo;
    @FXML
    private JFXButton btnActualizar;
    @FXML
    private AnchorPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translate();
        fecha.setValue(LocalDate.now());
    }

    ObservableList<clientes> lista;
    conectar con = new conectar();
    Connection cn;
    int valor = 0, idCliente;
    double xof=0,yof=0;
    
    private void translate(){
        parent.setOnMousePressed((event) -> {
            xof=event.getSceneX();
            yof=event.getSceneY();
        });
        
        parent.setOnMouseDragged((event) -> {
            ClientesController.stage.setX(event.getScreenX() - xof );
            ClientesController.stage.setY(event.getScreenY() - yof );
        });
    }

    public void start(ObservableList<clientes> lista, clientes c, String in) {
        if (in.equals("EDITAR")) {
            System.out.println("vamos a editar");
            titulo.setText("Editar Cliente");
            btnActualizar.setVisible(true);
            btnAgregar.setVisible(false);

            idCliente = c.getIdCliente();

            cliente.setText(c.getCliente());
            documento.setText(c.getDocumento() + "");
            telefono.setText(c.getTelefono() + "");
            residencia.setText(c.getResidencia());
            correo.setText(c.getCorreo());
            adicional.setText(c.getAdicional());
            fecha.setValue(c.getFecha().toLocalDate());
            contador.setText(adicional.getText().length() + "/300");
        } else {
            this.lista = lista;
        }

    }

    public int getValor() {
        return valor;
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
    private void addCliente(ActionEvent event) {
        if (cliente.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("El nombre del cliente es obligatorio")
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
                PreparedStatement ps = cn.prepareStatement("INSERT INTO clientes(cliente,documento,telefono,residencia,correo,fecha,adicional) VALUES (?,?,?,?,?,?,?)");
                ps.setString(1, metodos.line(cliente.getText()));
                if (documento.getText().isEmpty()) {
                    ps.setString(2, null);
                } else {
                    ps.setString(2, documento.getText());
                }
                if (telefono.getText().isEmpty()) {
                    ps.setString(3, null);
                } else {
                    ps.setString(3, telefono.getText());
                }
                ps.setString(4, residencia.getText());
                ps.setString(5, correo.getText());
                ps.setString(6, fecha.getValue().toString());
                ps.setString(7, adicional.getText());
                ps.executeUpdate();
                valor = 1;

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El cliente " + cliente.getText() + " ha sido agregado a la lista.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_CENTER);
                n.show();

                cliente.setText("");
                documento.setText("");
                residencia.setText("");
                telefono.setText("");
                correo.setText("");
                adicional.setText("");
                fecha.setValue(LocalDate.now());
                contador.setText("0/300");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }

        }

    }

    @FXML
    private void editCliente(ActionEvent event) {
        if (cliente.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("El nombre del cliente es obligatorio")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {

            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("UPDATE clientes SET cliente=?,documento=?,telefono=?,residencia=?,correo=?,fecha=?,adicional=? WHERE id_cliente=?");
                ps.setString(1, metodos.line(cliente.getText()));
                if (documento.getText().isEmpty()) {
                    ps.setString(2, null);
                } else {
                    ps.setString(2, documento.getText());
                }
                if (telefono.getText().isEmpty()) {
                    ps.setString(3, null);
                } else {
                    ps.setString(3, telefono.getText());
                }
                ps.setString(4, residencia.getText());
                ps.setString(5, correo.getText());
                ps.setString(6, fecha.getValue().toString());
                ps.setString(7, adicional.getText());
                ps.setInt(8, idCliente);
                ps.executeUpdate();
                valor = 1;

                Stage mystage = (Stage) this.cliente.getScene().getWindow();
                mystage.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }
        }

    }

    @FXML
    private void cerraModal(MouseEvent event) {
        Stage mystage = (Stage) this.cliente.getScene().getWindow();
        mystage.close();
    }


}
