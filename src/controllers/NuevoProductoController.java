package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.metodos;
import data.productos;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class NuevoProductoController implements Initializable {

    @FXML
    private JFXTextField producto;
    @FXML
    private JFXButton btnAgregar;
    @FXML
    private JFXTextArea descripcion;
    @FXML
    private Label contador;
    @FXML
    private JFXTextField venta;
    @FXML
    private JFXComboBox<String> categoria;
    @FXML
    private JFXTextField compra;
    @FXML
    private JFXTextField stock;
    @FXML
    private JFXTextField codigo;
    @FXML
    private JFXButton btnActualizar;
    @FXML
    private Label titulo;
    @FXML
    private JFXCheckBox c1;
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private Label mensaje;
    @FXML
    private AnchorPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translate();
        combo();
    }

    ObservableList<productos> lista;
    conectar con = new conectar();
    Connection cn;
    int valor = 0, idProducto;
    double xof=0,yof=0;
    
    private void translate(){
        parent.setOnMousePressed((event) -> {
            xof=event.getSceneX();
            yof=event.getSceneY();
        });
        
        parent.setOnMouseDragged((event) -> {
            ProductosController.stage.setX(event.getScreenX() - xof );
            ProductosController.stage.setY(event.getScreenY() - yof );
        });
    }

    private void combo() {
        ObservableList combo = FXCollections.observableArrayList();

        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM categorias");
            while (rs.next()) {
                combo.add(rs.getInt("id_categoria") + " - " + rs.getString("categoria"));
            }
            categoria.setItems(combo);
            categoria.setValue("1 - CATEGORIA GENERAL");
        } catch (SQLException ex) {
            Logger.getLogger(NuevoProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void start(ObservableList<productos> lista, productos p, String in) {
        if (in.equals("EDITAR")) {
            titulo.setText("Editar Producto");
            btnActualizar.setVisible(true);
            btnAgregar.setVisible(false);
            c1.setVisible(false);

            idProducto = p.getId();

            producto.setText(p.getProducto());
            stock.setText(p.getStock() + "");
            venta.setText(p.getPrecioVenta());
            compra.setText(p.getPrecioCompra());
            codigo.setText(p.getCodigo());
            descripcion.setText(p.getDescripcion());
            categoria.setValue(p.getIdCategoria());
            contador.setText(descripcion.getText().length() + "/500");

        } else {
            this.lista = lista;
        }

    }

    public int getValor() {
        return valor;
    }

    @FXML
    private void cerrar(MouseEvent event) {
        Stage mystage = (Stage) this.producto.getScene().getWindow();
        mystage.close();
    }

    @FXML
    private void agregarProducto(ActionEvent event) {
        if (producto.getText().isEmpty() || venta.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nombre del producto y su precio de venta\nLos campos de texto con asteriscos son obligatorios.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.CENTER);
            n.show();
        } else {

            if (!codigo.getText().isEmpty()) {
                for (int i = 0; i < lista.size(); i++) {
                    if (codigo.getText().equals(lista.get(i).getCodigo())) {
                        Notifications n = Notifications.create()
                                .title("Información")
                                .text("El codigo de producto ingresado ya se encuentra registrado.")
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
                PreparedStatement ps = cn.prepareStatement("INSERT INTO productos(codigo,producto,precio_venta,precio_compra,idCategoria,descripcion,stock) VALUES (?,?,?,?,?,?,?)");
                if (c1.isSelected()) {
                    ps.setString(1, metodos.cadenaAleatoria(spinner.getValue()));
                } else {
                    ps.setString(1, codigo.getText());
                }
                ps.setString(2, metodos.line(producto.getText()));
                ps.setString(3, venta.getText().replace(".", ""));
                if (compra.getText().isEmpty()) {
                    ps.setString(4, "0");
                } else {
                    ps.setString(4, compra.getText().replace(".", ""));
                }
                ps.setString(5, categoria.getValue().substring(0, 1));
                ps.setString(6, descripcion.getText());
                if (stock.getText().isEmpty()) {
                    ps.setInt(7, 0);
                } else {
                    ps.setString(7, stock.getText());
                }
                ps.executeUpdate();
                valor = 1;

                Notifications n = Notifications.create()
                        .title("Información")
                        .text("El producto '" + producto.getText() + "' ha sido agregado a la lista.")
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_CENTER);
                n.show();

                producto.setText("");
                venta.setText("");
                compra.setText("");
                stock.setText("");
                codigo.setText("");
                descripcion.setText("");
                categoria.setValue("1 - CATEGORIA GENERAL");
                contador.setText("0/500");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }

        }
    }

    @FXML
    private void actualizarProducto(ActionEvent event) {
        if (producto.getText().isEmpty() || venta.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Ingrese el nombre del producto y su precio de venta\nLos campos de texto con asteriscos son obligatorios.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.CENTER);
            n.show();
        } else {

            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement("UPDATE productos SET codigo=?,producto=?,precio_venta=?,precio_compra=?,idCategoria=?,descripcion=?,stock=? WHERE id_producto=?");
                ps.setString(1, codigo.getText());
                ps.setString(2, metodos.line(producto.getText()));
                ps.setString(3, venta.getText().replace(".", ""));
                if (compra.getText().isEmpty()) {
                    ps.setString(4, null);
                } else {
                    ps.setString(4, compra.getText().replace(".", ""));
                }
                ps.setString(5, categoria.getValue().substring(0, 1));
                ps.setString(6, descripcion.getText());
                if (stock.getText().isEmpty()) {
                    ps.setInt(7, 0);
                } else {
                    ps.setString(7, stock.getText());
                }
                ps.setInt(8, idProducto);
                ps.executeUpdate();
                valor = 1;

                Stage mystage = (Stage) this.producto.getScene().getWindow();
                mystage.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    @FXML
    private void codigo(ActionEvent event) {
        if (c1.isSelected()) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, 5);
            spinner.setValueFactory(valueFactory);
            mensaje.setVisible(true);
            spinner.setVisible(true);
            codigo.setDisable(true);
        } else {
            mensaje.setVisible(false);
            spinner.setVisible(false);
            codigo.setDisable(false);
        }
    }

    @FXML
    private void tipadoDescripcion(KeyEvent event) {
        if (descripcion.getText().length() > 499) {
            event.consume();
        }
        contador.setText(descripcion.getText().length() + "/500");
    }

    @FXML
    private void tipadoVenta(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '.' || c > '9') {
            event.consume();
        } else if (venta.getText().length() > 8) {
            event.consume();
        }
    }

    @FXML
    private void tipadoCompra(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '.' || c > '9') {
            event.consume();
        } else if (compra.getText().length() > 8) {
            event.consume();
        }
    }

    @FXML
    private void tipadoStock(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '0' || c > '9') {
            event.consume();
        } else if (stock.getText().length() > 5) {
            event.consume();
        }
    }

    @FXML
    private void setData(ActionEvent event) {
        String va = categoria.getValue();
        switch (va.substring(4)) {
            case "SMART PHONES":
                descripcion.setText("Marca:\nModelo:\nColor:\nTamaño de pantalla:\nTipo de pantalla:\nProcesador:\nRAM:\nROM:\nBatería:\nSistema operativo:\nBandas:\nCarga rapida?:");
                contador.setText(descripcion.getText().length() + "/500");
                break;   
            case "VEHICULOS":
                descripcion.setText("Marca:\nModelo:\nAño:\nColor:\nMotor:\nTransmisión:\nN° placa\nVelocidad maxima:\nDistancia recorrida:");
                contador.setText(descripcion.getText().length() + "/500");
                break;
            case "ELECTRODOMESTICOS":
                descripcion.setText("Fabricante:\nMarca:\nModelo:\nColor:\nFunciones:");
                contador.setText(descripcion.getText().length() + "/500");
                break;
            case "VESTIMENTA":
                descripcion.setText("\nMarca:\nModelo:\nColor:\nTalla:\nGenero:\nMaterial principal:");
                contador.setText(descripcion.getText().length() + "/500");
                break;  
            default:
                descripcion.setText("");
                contador.setText("0/500");
                break;
        }
    }

}
