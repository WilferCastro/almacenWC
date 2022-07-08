package controllers;

import com.jfoenix.controls.JFXButton;
import data.detalle_producto;
import data.metodos;
import data.ventas;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;

public class InformeController implements Initializable {

    @FXML
    private TableColumn<ventas, Integer> c_id;
    @FXML
    private TableColumn<ventas, String> c_cliente;
    @FXML
    private TableColumn<ventas, String> c_vendedor;
    @FXML
    private TableColumn<ventas, String> c_fecha;
    @FXML
    private TableColumn<ventas, String> c_subtotal;
    @FXML
    private TableColumn<ventas, String> c_total;
    @FXML
    private TableColumn<ventas, String> c_descuento;
    @FXML
    private TableColumn<ventas, String> c_domicilio;
    @FXML
    private TableView<ventas> tabla1;
    @FXML
    private AnchorPane panel1;
    @FXML
    private TableView<detalle_producto> tabla2;
    @FXML
    private TableColumn<detalle_producto, Integer> p_id;
    @FXML
    private TableColumn<detalle_producto, String> p_codigo;
    @FXML
    private TableColumn<detalle_producto, String> p_producto;
    @FXML
    private TableColumn<detalle_producto, String> p_precio;
    @FXML
    private TableColumn<detalle_producto, String> p_cantidad;
    @FXML
    private TableColumn<detalle_producto, String> p_total;
    @FXML
    private Label labelCompras;
    @FXML
    private Label labelVentas;
    @FXML
    private JFXButton btnOcultar;
    @FXML
    private JFXButton btnAnular;
    @FXML
    private Label labeltext;
    @FXML
    private Label fecha;
    @FXML
    private Label verde;
    @FXML
    private Label morado;
    @FXML
    private Label azul;
    @FXML
    private Label naranja;
    @FXML
    private Label rojo;
    @FXML
    private Label amarillo;
    @FXML
    private PieChart grafica;
    @FXML
    private Label contador;
    @FXML
    private JFXButton btnEstadisticas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fecha.setText(metodos.fecha(LocalDate.now()));
        estadisticas("SELECT COUNT(usuario) FROM ventas WHERE anulada=0 AND fecha='" + fecha.getText() + "'", "Ventas\n", verde);
        estadisticas("SELECT COUNT(usuario) FROM compras WHERE anulada=0 AND fecha='" + fecha.getText() + "'", "Compras\n", morado);
        estadisticas("SELECT SUM(total) FROM ventas WHERE anulada=0 AND fecha='" + fecha.getText() + "'", "Monto Ventas\n$", azul);
        estadisticas("SELECT SUM(total) FROM compras WHERE anulada=0 AND fecha='" + fecha.getText() + "'", "Monto Compras\n$", rojo);
        estadisticas("SELECT COUNT(producto) FROM detalle_venta WHERE fecha='" + fecha.getText() + "'", "Productos vendidos: ", naranja);
        estadisticas("SELECT COUNT(producto) FROM detalle_compra WHERE fecha='" + fecha.getText() + "'", "Productos comprados: ", amarillo);
        grafica();
        loadVentas("SELECT * FROM ventas WHERE anulada=0 AND fecha='" + fecha.getText() + "'");
    }

    ObservableList<ventas> lista;
    ObservableList<detalle_producto> lista2;
    conectar con = new conectar();
    Connection cn;
    DecimalFormat df = new DecimalFormat();
    String aux = "venta";
    int index = -1;

    public void start(String data) {
        if (data.equals("compras")) {
            labelCompras.setTranslateX(-146);
            labelCompras.setStyle("-fx-text-fill: #905aa1; -fx-cursor: hand");
            labeltext.setStyle("-fx-text-fill: #905aa1; -fx-cursor: hand");
            labelVentas.setVisible(false);
            labelCompras.setDisable(true);

            loadCompras("SELECT * FROM compras WHERE anulada=0 AND fecha='" + fecha.getText() + "'");
            aux = "compra";
            btnAnular.setText("Anular compra");
            btnEstadisticas.setVisible(false);
        }else{
            labelCompras.setVisible(false);
            labelVentas.setDisable(true);
            btnEstadisticas.setVisible(false);
        }
    }
    
    private void grafica() {
        ObservableList list = FXCollections.observableArrayList();

        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery("SELECT producto, COUNT( producto ) AS total FROM detalle_venta WHERE fecha='"+fecha.getText()+"' GROUP BY producto HAVING COUNT(*) > 1");
            while (rs.next()) {
                list.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
            }
            grafica.setData(list);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }

    }

    private void loadVentas(String sql) {
        try {
            lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new ventas(rs.getInt("id_venta"), rs.getString("cliente"), rs.getString("usuario"), rs.getString("fecha"), df.format(rs.getInt("subtotal")), df.format(rs.getInt("total")), rs.getString("descuento"), rs.getString("domicilio")));
            }

            c_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            c_cliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
            c_vendedor.setCellValueFactory(new PropertyValueFactory<>("vendedor"));
            c_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            c_subtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
            c_total.setCellValueFactory(new PropertyValueFactory<>("total"));
            c_descuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
            c_domicilio.setCellValueFactory(new PropertyValueFactory<>("domicilio"));

            tabla1.setItems(lista);
            contador.setText(lista.size()+"");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    private void loadCompras(String sql) {
        try {
            lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new ventas(rs.getInt("id_compra"), rs.getString("proveedor"), rs.getString("usuario"), rs.getString("fecha"), "", df.format(rs.getInt("total")), rs.getString("comprobante"), ""));
            }

            c_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            c_cliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
            c_vendedor.setCellValueFactory(new PropertyValueFactory<>("vendedor"));
            c_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            c_total.setCellValueFactory(new PropertyValueFactory<>("total"));
            c_descuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));

            tabla1.setItems(lista);
            contador.setText(lista.size()+"");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    private void loadDetails(String sql, String num) {
        try {
            lista2 = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista2.add(new detalle_producto(rs.getInt(num), rs.getString("codigo"), rs.getString("producto"), rs.getString("cantidad"), df.format(rs.getInt("precio")), df.format(rs.getInt("total"))));
            }

            p_id.setCellValueFactory(new PropertyValueFactory<>("id_producto"));
            p_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            p_producto.setCellValueFactory(new PropertyValueFactory<>("producto"));
            p_precio.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            p_cantidad.setCellValueFactory(new PropertyValueFactory<>("precio"));
            p_total.setCellValueFactory(new PropertyValueFactory<>("total"));

            tabla2.setItems(lista2);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    private void estadisticas(String sql, String name, Label l) {
        try {
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                l.setText(name + "" + df.format(rs.getInt(1)));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @FXML
    private void ocultarPanel(ActionEvent event) {
        panel1.setVisible(false);
    }

    @FXML
    private void getDetails(MouseEvent event) {
        index = tabla1.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            if (aux.equals("venta")) {
                loadDetails("SELECT * FROM detalle_venta WHERE numero_venta=" + c_id.getCellData(index), "numero_venta");
            } else {
                loadDetails("SELECT * FROM detalle_compra WHERE numero_compra=" + c_id.getCellData(index), "numero_compra");
            }
            btnOcultar.setVisible(true);
            btnAnular.setVisible(true);
        }
    }

    @FXML
    private void cargarCompras(MouseEvent event) {

        labelCompras.setTranslateX(-146);
        labelCompras.setStyle("-fx-text-fill: #905aa1; -fx-cursor: hand");
        labelVentas.setTranslateX(156);
        labelVentas.setStyle("-fx-text-fill: #000; -fx-cursor: hand");
        labeltext.setStyle("-fx-text-fill: #905aa1; -fx-cursor: hand");

        c_subtotal.setVisible(false);
        c_domicilio.setVisible(false);

        c_cliente.setPrefWidth(195);
        c_vendedor.setPrefWidth(195);
        c_fecha.setPrefWidth(195);
        c_total.setPrefWidth(194);
        c_descuento.setPrefWidth(194);

        c_descuento.setText("N° de recibo");
        c_cliente.setText("Proveedor");

        loadCompras("SELECT * FROM compras WHERE anulada=0 AND fecha='" + fecha.getText() + "'");
        aux = "compra";
        btnAnular.setText("Anular compra");
        btnAnular.setVisible(false);
        btnOcultar.setVisible(false);
    }

    @FXML
    private void cargarVentas(MouseEvent event) {

        labelCompras.setTranslateX(0);
        labelCompras.setStyle("-fx-text-fill: #000; -fx-cursor: hand");
        labelVentas.setTranslateX(0);
        labelVentas.setStyle("-fx-text-fill: #52b36e; -fx-cursor: hand");
        labeltext.setStyle("-fx-text-fill: #52b36e; -fx-cursor: hand");

        c_subtotal.setVisible(true);
        c_domicilio.setVisible(true);

        c_cliente.setPrefWidth(170);
        c_vendedor.setPrefWidth(139);
        c_fecha.setPrefWidth(172);
        c_total.setPrefWidth(120);
        c_descuento.setPrefWidth(126);

        c_descuento.setText("Descuento");
        c_cliente.setText("Cliente");

        loadVentas("SELECT * FROM ventas WHERE anulada=0 AND fecha='" + fecha.getText() + "'");
        aux = "venta";
        btnAnular.setText("Anular venta");
        btnAnular.setVisible(false);
        btnOcultar.setVisible(false);
    }

    @FXML
    private void hideDetails(ActionEvent event) {
        lista2.clear();
        tabla1.getSelectionModel().clearSelection();
        btnOcultar.setVisible(false);
        btnAnular.setVisible(false);
    }

    private void anul(String sql, String msg1, String msg2, String aux) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("CONFIRMAR.");
        a.setContentText(msg1);
        a.setHeaderText(null);
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                cn = con.conexion();
                PreparedStatement ps = cn.prepareStatement(sql);
                ps.executeUpdate();
                Notifications n = Notifications.create()
                        .title("Información")
                        .text(msg2)
                        .graphic(new ImageView(new Image("/images/good.png")))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER);
                n.show();

                if (aux.equals("venta")) {
                    loadVentas("SELECT * FROM ventas WHERE anulada=0 AND fecha='" + fecha.getText() + "'");
                } else {
                    loadCompras("SELECT * FROM compras WHERE anulada=0 AND fecha='" + fecha.getText() + "'");
                }

                lista2.clear();
                btnOcultar.setVisible(false);
                btnAnular.setVisible(false);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                con.cerrarConexion();
            }
        }
    }

    @FXML
    private void anularVeCo(ActionEvent event) {
        if (aux.equals("venta")) {
            anul("UPDATE ventas SET anulada=1 WHERE id_venta=" + c_id.getCellData(index), "Desea anular la venta N° " + c_id.getCellData(index) + " ?.", "La venta N° '" + c_id.getCellData(index) + "' ha sido anulada.", "venta");
        } else {
            anul("UPDATE compras SET anulada=1 WHERE id_compra=" + c_id.getCellData(index), "Desea anular la compra N° " + c_id.getCellData(index) + " ?.", "La compra N° '" + c_id.getCellData(index) + "' ha sido anulada.", "compra");
        }
    }

    @FXML
    private void verPanel(ActionEvent event) {
        panel1.setVisible(true);
    }

}
