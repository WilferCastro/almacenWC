package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.detalles;
import data.metodos;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class ComprasController implements Initializable {

    @FXML
    private JFXComboBox<String> producto;
    @FXML
    private JFXTextArea descripcion;
    @FXML
    private JFXTextField precio;
    @FXML
    private JFXTextField cantidad;
    @FXML
    private JFXTextField buscador;
    @FXML
    private Label stock;
    @FXML
    private Label textoStock;
    @FXML
    private Label codigo;
    @FXML
    private Label idProducto;
    @FXML
    private TableView<detalles> tabla;
    @FXML
    private TableColumn<detalles, String> c_codigo;
    @FXML
    private TableColumn<detalles, String> c_producto;
    @FXML
    private TableColumn<detalles, String> c_precio;
    @FXML
    private TableColumn<detalles, Integer> c_cantidad;
    @FXML
    private TableColumn<detalles, Integer> c_total;
    @FXML
    private TableColumn<detalles, Integer> c_stock;
    @FXML
    private TableColumn<detalles, Integer> c_IdProducto;
    @FXML
    private Label items;
    @FXML
    private JFXButton btnRemover;
    @FXML
    private JFXButton btnCancelar2;
    @FXML
    private Label total;
    @FXML
    private JFXButton btnValidar;
    @FXML
    private JFXButton btnCancelar1;
    @FXML
    private JFXComboBox<String> proveedor;
    @FXML
    private Label numVenta;
    @FXML
    private Label fecha;
    @FXML
    private Label usuario;
    @FXML
    private JFXTextField comprobante;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fecha.setText(metodos.fecha(LocalDate.now()));
        usuario.setText(LoginController.usuario);
        number();
        combo("SELECT producto FROM productos WHERE stock <= " + menor + " ORDER BY producto", producto);
        combo("SELECT proveedor FROM proveedores ORDER BY proveedor", proveedor);
    }

    conectar con = new conectar();
    Connection cn;
    ObservableList list;
    ObservableList<detalles> info = FXCollections.observableArrayList();
    DecimalFormat df = new DecimalFormat();

    int tot = 0, index = -1, menor = 0, increment = 0, alert;

    private void combo(String sql, JFXComboBox combo) {
        try {
            list = FXCollections.observableArrayList();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            combo.setItems(list);

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void number() {
        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT COUNT(id_compra) FROM compras");
            while (rs.next()) {
                numVenta.setText((rs.getInt(1) + 1) + "");
            }

            rs = cn.createStatement().executeQuery("SELECT stockCompra,autoCompra,alertCompra FROM opciones WHERE id_opcion=1");
            while (rs.next()) {
                menor = rs.getInt("stockCompra");
                increment = rs.getInt("autoCompra");
                alert = rs.getInt("alertCompra");
            }

            if (increment == 0) {
                stock.setVisible(false);
                textoStock.setVisible(false);
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void can1() {
        codigo.setText("...");
        idProducto.setText("...");
        producto.setValue(null);
        cantidad.setText("");
        precio.setText("");
        descripcion.setText("");
        buscador.setText("");

        stock.setStyle("-fx-text-fill: #000");
        stock.setText("0");
    }
    
    private void addTable() {
        if (producto.getValue() == null || cantidad.getText().isEmpty() || precio.getText().isEmpty()) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("Seleccione el producto, ingrese su precio y la cantidad.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        }else {

            for (int i = 0; i < info.size(); i++) {
                if (Integer.parseInt(idProducto.getText()) == info.get(i).getIdProducto()) {
                    Notifications n = Notifications.create()
                            .title("Información")
                            .text("El producto ya se encuentra en la lista.")
                            .graphic(new ImageView(new Image("/images/info.png")))
                            .hideAfter(Duration.seconds(3))
                            .position(Pos.CENTER);
                    n.show();
                    return;
                }
            }

            int p = Integer.parseInt(precio.getText().replace(".", ""));
            int cant = Integer.parseInt(cantidad.getText());
            tot += p * cant;

            total.setText(df.format(tot));

            info.add(new detalles(codigo.getText(), producto.getValue(), df.format(p), cant, p * cant, Integer.parseInt(stock.getText()), Integer.parseInt(idProducto.getText())));

            c_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            c_producto.setCellValueFactory(new PropertyValueFactory<>("producto"));
            c_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
            c_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            c_total.setCellValueFactory(new PropertyValueFactory<>("total"));
            c_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            c_IdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));

            tabla.setItems(info);
            items.setText(info.size() + "");
            can1();
            btnCancelar1.setDisable(false);
            btnValidar.setDisable(false);
        }
    }
    
    

    @FXML
    private void establecerData(ActionEvent event) {
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM productos WHERE producto='" + producto.getValue() + "'");
            while (rs.next()) {
                if (rs.getInt("stock") > 5) {
                    stock.setStyle("-fx-text-fill: blue");
                } else {
                    stock.setStyle("-fx-text-fill: red");
                }

                idProducto.setText(rs.getString("id_producto"));
                codigo.setText(rs.getString("codigo"));
                precio.setText(df.format(rs.getInt("precio_compra")));
                stock.setText(rs.getString("stock"));
                descripcion.setText(rs.getString("descripcion"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void irCantidad(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            cantidad.requestFocus();
        }
    }

    @FXML
    private void tipadoPrecio(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '.' || c > '9') {
            event.consume();
        } else if (precio.getText().length() > 8) {
            event.consume();
        }
    }

    @FXML
    private void addTabla(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            addTable();
        }
    }

    @FXML
    private void tipadoCantidad(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '0' || c > '9') {
            event.consume();
        } else if (cantidad.getText().length() > 6) {
            event.consume();
        }
    }

    @FXML
    private void buscarProducto(KeyEvent event) {
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM productos WHERE producto LIKE '%" + buscador.getText() + "%' AND stock <=" + menor);
            while (rs.next()) {
                if (rs.getInt("stock") > 5) {
                    stock.setStyle("-fx-text-fill: blue");
                } else {
                    stock.setStyle("-fx-text-fill: red");
                }
                producto.setValue(rs.getString("producto"));
                idProducto.setText(rs.getString("id_producto"));
                codigo.setText(rs.getString("codigo"));
                precio.setText(df.format(rs.getInt("precio_compra")));
                stock.setText(rs.getString("stock"));
                descripcion.setText(rs.getString("descripcion"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void calcular(ActionEvent event) {
        addTable();
    }

    @FXML
    private void activarBotons(MouseEvent event) {
        index = tabla.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            btnCancelar2.setVisible(true);
            btnRemover.setVisible(true);
        }
    }

    @FXML
    private void removerTabla(ActionEvent event) {
        int valor = c_total.getCellData(index);
        tot -= valor;

        total.setText(df.format(tot));

        tabla.getSelectionModel().clearSelection();
        info.remove(index);
        if (info.isEmpty()) {
            btnCancelar1.setDisable(true);
            btnValidar.setDisable(true);
        }
        items.setText(info.size() + "");
        btnCancelar2.setVisible(false);
        btnRemover.setVisible(false);
    }

    @FXML
    private void cancelarTabla(ActionEvent event) {
        tabla.getSelectionModel().clearSelection();
        btnCancelar2.setVisible(false);
        btnRemover.setVisible(false);
    }
    
    private void can2() {
        tot = 0;
        index = -1;

        info.clear();
        btnValidar.setDisable(true);
        btnCancelar1.setDisable(true);

        items.setText("0");
        total.setText("0");
        comprobante.setText("");
        proveedor.setValue(null);
    }
    
    private void guardarVenta() {
        try {

            PreparedStatement ps;

            ps = cn.prepareStatement("INSERT INTO compras(comprobante,proveedor,usuario,fecha,total,anulada) VALUES (?,?,?,?,?,?)");
            ps.setString(1, comprobante.getText());
            ps.setString(2, proveedor.getValue());
            ps.setString(3, usuario.getText());
            ps.setString(4, fecha.getText());
            ps.setInt(5, tot);
            ps.setInt(6, 0);
            ps.executeUpdate();

            for (int i = 0; i < info.size(); i++) {

                ps = cn.prepareStatement("INSERT INTO detalle_compra(numero_compra,codigo,producto,cantidad,precio,total,fecha) VALUES (?,?,?,?,?,?,?)");
                ps.setInt(1, Integer.parseInt(numVenta.getText()));
                ps.setString(2, c_codigo.getCellData(i) + "");
                ps.setString(3, c_producto.getCellData(i));
                ps.setInt(4, c_cantidad.getCellData(i));
                ps.setString(5, c_precio.getCellData(i).replace(".", ""));
                ps.setInt(6, c_total.getCellData(i));
                ps.setString(7, fecha.getText());
                ps.executeUpdate();

                if (increment == 1) {
                    ps = cn.prepareStatement("UPDATE productos SET stock='" + (c_stock.getCellData(i) + c_cantidad.getCellData(i)) + "' WHERE id_producto='" + c_IdProducto.getCellData(i) + "'");
                    ps.executeUpdate();
                }

            }

            Notifications n = Notifications.create()
                    .title("Información")
                    .text("La compra ha sido registrada.")
                    .graphic(new ImageView(new Image("/images/good.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.TOP_CENTER);
            n.show();

            int v = Integer.parseInt(numVenta.getText()) + 1;
            numVenta.setText(v + "");
            can2();

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void confirmarCompra(ActionEvent event) {
         if (alert == 1) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Información.");
            a.setContentText("COMPRA N° "+numVenta.getText()+"\n"
                    + "TOTAL: $"+total.getText()+"\n"
                    + "CANTIDAD DE PRODUCTOS: "+items.getText()+"\n\n"
                    + "PROVEEDOR: "+proveedor.getValue()+"\n"
                    + "FECHA: "+fecha.getText()+"\n"
                    + "VENDEDOR: "+usuario.getText());
            a.setHeaderText(null);
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                guardarVenta();
            }
        }else{
            guardarVenta();
        }
    }

    @FXML
    private void cancelarCompra(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar.");
        a.setContentText("Desea cancelar la compra?");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            can2();
        }
    }

    @FXML
    private void abrirCalculadora(ActionEvent event) {
        try {
            Runtime r = Runtime.getRuntime();
            r.exec("C:\\Windows\\system32\\win32calc.exe");
        } catch (IOException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void anularCompras(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/informe.fxml"));
            Parent root = loader.load();

            InformeController i=loader.getController();
            i.start("compras");
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Informe de ventas");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
