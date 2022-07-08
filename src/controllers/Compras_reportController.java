
package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import data.detalle_producto;
import data.metodos;
import data.ventas;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;

public class Compras_reportController implements Initializable {

    @FXML
    private TableView<ventas> tabla1;
    @FXML
    private TableColumn<ventas, Integer> c_id;
    @FXML
    private TableColumn<ventas, String> c_cliente;
    @FXML
    private TableColumn<ventas, String> c_vendedor;
    @FXML
    private TableColumn<ventas, String> c_fecha;
    @FXML
    private TableColumn<ventas, String> c_total;
    @FXML
    private TableColumn<ventas, String> c_descuento;
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
    private Label num_detalle;
    @FXML
    private JFXButton btnDetalles;
    @FXML
    private JFXDatePicker datepick;
    @FXML
    private JFXComboBox<String> combo;
    @FXML
    private JFXComboBox<String> combo2;
    @FXML
    private Label contador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        combo.setItems(com);
    }    
    
    ObservableList<ventas> lista;
    ObservableList<detalle_producto> lista2;
    ObservableList com = FXCollections.observableArrayList("HOY","COMPRAS ANULADAS (HOY)","COMPRAS ANULADAS (ESTE MES)","TODAS LAS COMPRAS ANULADAS","FILTRAR POR MES","MOSTRAR TODAS LAS COMPRAS");
    conectar con = new conectar();
    Connection cn;
    DecimalFormat df = new DecimalFormat();
    int index = -1;
    
    private void loadVentas(String sql) {
        try {
            lista = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista.add(new ventas(rs.getInt("id_compra"), rs.getString("proveedor"), rs.getString("usuario"), rs.getString("fecha"), df.format(rs.getInt("total")), rs.getString("comprobante"), "", ""));
            }

            c_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            c_cliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
            c_vendedor.setCellValueFactory(new PropertyValueFactory<>("vendedor"));
            c_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            c_total.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
            c_descuento.setCellValueFactory(new PropertyValueFactory<>("total"));

            tabla1.setItems(lista);
            contador.setText(lista.size()+"");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    private void loadDetails(String sql) {
        try {
            lista2 = FXCollections.observableArrayList();
            cn = con.conexion();
            ResultSet rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                lista2.add(new detalle_producto(rs.getInt("numero_compra"), rs.getString("codigo"), rs.getString("producto"), df.format(rs.getInt("precio")), rs.getString("cantidad"), df.format(rs.getInt("total"))));
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

    @FXML
    private void loadDetails(MouseEvent event) {
        index = tabla1.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            loadDetails("SELECT * FROM detalle_compra WHERE numero_compra=" + c_id.getCellData(index));
            num_detalle.setText(c_id.getCellData(index) + "");
            btnDetalles.setVisible(true);
        }
    }

    @FXML
    private void ocultarDetalles(ActionEvent event) {
        tabla1.getSelectionModel().clearSelection();
        lista2.clear();
        btnDetalles.setVisible(false);
        num_detalle.setText("...");
    }

    @FXML
    private void loadDetailsFecha(ActionEvent event) {
        loadVentas("SELECT * FROM compras WHERE anulada=0 AND fecha='" + metodos.fecha(datepick.getValue()) + "'");
        combo2.setVisible(false);
    }

    @FXML
    private void loadMoreDetails(ActionEvent event) {
        combo2.setVisible(false);
        switch (combo.getValue()) {
            case "HOY":
                loadVentas("SELECT * FROM compras WHERE anulada=0 AND fecha='" + metodos.fecha(LocalDate.now()) + "'");
                break;
            case "COMPRAS ANULADAS (HOY)":
                loadVentas("SELECT * FROM compras WHERE anulada=1 AND fecha='" + metodos.fecha(LocalDate.now()) + "'");
                break;     
            case "COMPRAS ANULADAS (ESTE MES)":
                loadVentas("SELECT * FROM compras WHERE anulada=1 AND fecha LIKE '%" + metodos.mes(LocalDate.now()) + "%'");
                break;  
            case "TODAS LAS COMPRAS ANULADAS":
                loadVentas("SELECT * FROM compras WHERE anulada=1");
                break;     
            case "MOSTRAR TODAS LAS COMPRAS":
                loadVentas("SELECT * FROM compras WHERE anulada=0");
                break;     
            default:
                com = FXCollections.observableArrayList("ENERO","FEBRERO", "MARZO", "ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE");
                combo2.setItems(com);
                combo2.setVisible(true);
                break;  
        }
    }

    @FXML
    private void filtroMes(ActionEvent event) {
        loadVentas("SELECT * FROM compras WHERE anulada=0 AND fecha LIKE '%"+combo2.getValue()+"%'");
    }
    
}
