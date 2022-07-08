package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class VentasController implements Initializable {

    @FXML
    private Label total;
    @FXML
    private Label subtotal;
    @FXML
    private CheckBox c1;
    @FXML
    private JFXCheckBox c2;
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
    private Label codigo;
    @FXML
    private Label idProducto;
    @FXML
    private Label items;
    @FXML
    private Label texto1;
    @FXML
    private Label texto1_desc;
    @FXML
    private Label texto2;
    @FXML
    private Label texto2_domi;
    @FXML
    private JFXButton btnRemover;
    @FXML
    private JFXButton btnCancelar2;
    @FXML
    private JFXButton btnValidar;
    @FXML
    private JFXButton btnCancelar1;
    @FXML
    private JFXComboBox<String> cliente;
    @FXML
    private Label fecha;
    @FXML
    private Label numVenta;
    @FXML
    private Label usuario;
    @FXML
    private Label textoStock;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fecha.setText(metodos.fecha(LocalDate.now()));
        usuario.setText(LoginController.usuario);
        number();
        combo("SELECT producto FROM productos WHERE stock >= " + mayor + " ORDER BY producto", producto);
        combo("SELECT cliente FROM clientes ORDER BY cliente", cliente);
    }

    conectar con = new conectar();
    Connection cn;
    ObservableList list;
    ObservableList<detalles> info = FXCollections.observableArrayList();
    DecimalFormat df = new DecimalFormat();

    int subt = 0, tot = 0, dom = 0, desc = 0, aux = 0, index = -1, mayor = 0, increment = 0, alert;

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
            ResultSet rs = cn.createStatement().executeQuery("SELECT COUNT(id_venta) FROM ventas");
            while (rs.next()) {
                numVenta.setText((rs.getInt(1) + 1) + "");
            }

            rs = cn.createStatement().executeQuery("SELECT stockVenta,autoVenta,alertVenta FROM opciones WHERE id_opcion=1");
            while (rs.next()) {
                mayor = rs.getInt("stockVenta");
                increment = rs.getInt("autoVenta");
                alert = rs.getInt("alertVenta");
            }

            if (increment == 0) {
                stock.setVisible(false);
                textoStock.setVisible(false);
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        } else if (Integer.parseInt(cantidad.getText()) > Integer.parseInt(stock.getText()) && increment == 1) {
            Notifications n = Notifications.create()
                    .title("Información")
                    .text("El producto no tiene el stock suficiente para la venta.")
                    .graphic(new ImageView(new Image("/images/info.png")))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.CENTER);
            n.show();
        } else {

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
            subt += p * cant;

            desc = subt * aux / 100;
            tot = (subt + dom) - desc;
            total.setText(df.format(tot));
            subtotal.setText(df.format(subt));
            if (c2.isSelected()) {
                texto1_desc.setText(df.format(desc) + " --> " + aux + "%");
            }

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
    private void calcular(ActionEvent event) {
        addTable();
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

    @FXML
    private void activarDomicilio(ActionEvent event) {
        if (c1.isSelected()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ingrese valor de envio.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ingrese el valor del envio.");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                try {
                    dom = Integer.parseInt(result.get().replace(".", ""));
                    tot += dom;

                    total.setText(df.format(tot));
                    texto2.setVisible(true);
                    texto2_domi.setText(df.format(dom));
                    texto2_domi.setVisible(true);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "El valor ingresado es incorrecto.\n\n" + e.getMessage());
                    c1.setSelected(false);
                }
            } else {
                c1.setSelected(false);
            }
        } else {
            tot -= dom;
            total.setText(df.format(tot));
            dom = 0;
            texto2.setVisible(false);
            texto2_domi.setVisible(false);
            texto2_domi.setText("No aplica");
        }
    }

    @FXML
    private void activarDescuento(ActionEvent event) {
        if (c2.isSelected()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ingrese un valor entre 1 y 99.");
            dialog.setHeaderText(null);
            dialog.setContentText("Ingrese valor del descuento: %");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().equals("")) {
                try {
                    aux = Integer.parseInt(result.get());

                    if (aux > 99 || aux < 1) {
                        JOptionPane.showMessageDialog(null, "Ingrese un valor entre 1 y 99.");
                        c2.setSelected(false);
                    } else {
                        desc = subt * aux / 100;
                        tot -= desc;
                        total.setText(df.format(tot));
                        texto1.setVisible(true);
                        texto1_desc.setText(df.format(desc) + " --> " + aux + "%");
                        texto1_desc.setVisible(true);
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "El valor ingresado es incorrecto.\n\n" + e.getMessage());
                    c2.setSelected(false);
                }
            } else {
                c2.setSelected(false);
            }
        } else {
            tot += desc;
            total.setText(df.format(tot));
            desc = 0;
            aux = 0;
            texto1.setVisible(false);
            texto1_desc.setVisible(false);
            texto1_desc.setText("No aplica");
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
                precio.setText(df.format(rs.getInt("precio_venta")));
                stock.setText(rs.getString("stock"));
                descripcion.setText(rs.getString("descripcion"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buscarProducto(KeyEvent event) {
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM productos WHERE producto LIKE '%" + buscador.getText() + "%' AND stock >=" + mayor);
            while (rs.next()) {
                if (rs.getInt("stock") > 5) {
                    stock.setStyle("-fx-text-fill: blue");
                } else {
                    stock.setStyle("-fx-text-fill: red");
                }
                producto.setValue(rs.getString("producto"));
                idProducto.setText(rs.getString("id_producto"));
                codigo.setText(rs.getString("codigo"));
                precio.setText(df.format(rs.getInt("precio_venta")));
                stock.setText(rs.getString("stock"));
                descripcion.setText(rs.getString("descripcion"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        subt -= valor;

        desc = subt * aux / 100;
        tot = (subt + dom) - desc;

        total.setText(df.format(tot));
        subtotal.setText(df.format(subt));
        if (c1.isSelected()) {
            texto1_desc.setText(df.format(desc));
        }

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

    @FXML
    private void irCantidad(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            cantidad.requestFocus();
        }
    }

    @FXML
    private void addTabla(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            addTable();
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
    private void tipadoPrecio(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c < '.' || c > '9') {
            event.consume();
        } else if (precio.getText().length() > 8) {
            event.consume();
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

    private void guardarVenta() {
        try {

            PreparedStatement ps;

            ps = cn.prepareStatement("INSERT INTO ventas(cliente,usuario,fecha,subtotal,total,descuento,domicilio,anulada) VALUES (?,?,?,?,?,?,?,?)");
            ps.setString(1, cliente.getValue());
            ps.setString(2, usuario.getText());
            ps.setString(3, fecha.getText());
            ps.setInt(4, subt);
            ps.setInt(5, tot);
            ps.setString(6, texto1_desc.getText());
            ps.setString(7, texto2_domi.getText());
            ps.setInt(8, 0);
            ps.executeUpdate();

            for (int i = 0; i < info.size(); i++) {

                ps = cn.prepareStatement("INSERT INTO detalle_venta(numero_venta,codigo,producto,cantidad,precio,total,fecha) VALUES (?,?,?,?,?,?,?)");
                ps.setInt(1, Integer.parseInt(numVenta.getText()));
                ps.setString(2, c_codigo.getCellData(i) + "");
                ps.setString(3, c_producto.getCellData(i));
                ps.setInt(4, c_cantidad.getCellData(i));
                ps.setString(5, c_precio.getCellData(i).replace(".", ""));
                ps.setInt(6, c_total.getCellData(i));
                ps.setString(7, fecha.getText());
                ps.executeUpdate();

                if (increment == 1) {
                    ps = cn.prepareStatement("UPDATE productos SET stock='" + (c_stock.getCellData(i) - c_cantidad.getCellData(i)) + "' WHERE id_producto='" + c_IdProducto.getCellData(i) + "'");
                    ps.executeUpdate();
                }

            }

            Notifications n = Notifications.create()
                    .title("Información")
                    .text("La venta ha sido registrada.")
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
    private void confirmarVenta(ActionEvent event) {
        if (alert == 1) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Información.");
            a.setContentText("VENTA N° "+numVenta.getText()+"\n"
                    + "TOTAL: $"+total.getText()+"\n"
                    + "SUBTOTAL: $"+subtotal.getText()+"\n"
                    + "DESCUENTO: "+texto1_desc.getText()+"\n"
                    + "ENVIO A DOMICILIO: "+texto2_domi.getText()+"\n"
                    + "CANTIDAD DE PRODUCTOS: "+items.getText()+"\n\n"
                    + "CLIENTE: "+cliente.getValue()+"\n"
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

    private void can2() {
        subt = tot = dom = desc = aux = 0;
        index = -1;

        info.clear();
        btnValidar.setDisable(true);
        btnCancelar1.setDisable(true);
        texto1.setVisible(false);
        texto1_desc.setText("No aplica");
        texto1_desc.setVisible(false);
        texto2.setVisible(false);
        texto2_domi.setText("No aplica");
        texto2_domi.setVisible(false);
        c1.setSelected(false);
        c2.setSelected(false);

        items.setText("0");
        total.setText("0");
        subtotal.setText("0");
        cliente.setValue(null);
    }

    @FXML
    private void cancelarVenta(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar.");
        a.setContentText("Desea cancelar la venta?");
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            can2();
        }
    }

    @FXML
    private void anularVentas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/informe.fxml"));
            Parent root = loader.load();
            
            InformeController i=loader.getController();
            i.start("ventas");

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
