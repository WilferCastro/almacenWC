package controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import data.metodos;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.swing.JOptionPane;

public class EstadisticasController implements Initializable {

    @FXML
    private Label azul1;
    @FXML
    private Label azul3;
    @FXML
    private Label azul4;
    @FXML
    private Label azul5;
    @FXML
    private JFXDatePicker fecha1;
    @FXML
    private JFXComboBox<String> comboMes;
    @FXML
    private Label lfecha1;
    @FXML
    private Label lfecha3;
    @FXML
    private Label lfecha4;
    @FXML
    private Label lfecha5;
    @FXML
    private Label labelVentas;
    @FXML
    private Label labelDia;
    @FXML
    private Label azul2;
    @FXML
    private Label lfecha2;
    @FXML
    private Label labelValor1;
    @FXML
    private Label labelValor3;
    @FXML
    private Label labelValor4;
    @FXML
    private Label labelValor5;
    @FXML
    private Label labelValor2;
    @FXML
    private PieChart grafica1;
    @FXML
    private JFXDatePicker fecha2;
    @FXML
    private Label labelDia2;
    @FXML
    private JFXComboBox<String> comboMes2;
    @FXML
    private Label labelNaranja;
    @FXML
    private Label labelP1;
    @FXML
    private Label labelAzul;
    @FXML
    private Label labelP2;
    @FXML
    private Label labelRojo;
    @FXML
    private Label labelPorcentaje;
    @FXML
    private AnchorPane panel2;
    @FXML
    private Label Plabel1;
    @FXML
    private Label Plabel2;
    @FXML
    private Label Plabel3;
    @FXML
    private Label Plabel4;
    @FXML
    private JFXComboBox<String> comboP2;
    @FXML
    private Label Plabel5;
    @FXML
    private Label Plabel6;
    @FXML
    private ImageView flechaAtras;
    @FXML
    private ImageView flechaAdelante;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboMes.setItems(list);
        comboMes2.setItems(list);
        list = FXCollections.observableArrayList("VENTAS REALIZADAS", "MONTO DE VENTAS", "COMPRAS REALIZADAS", "MONTO DE COMPRAS");
        comboP2.setItems(list);
        porcentaje();
        grafica("SELECT producto, COUNT(*) Total FROM detalle_venta GROUP BY producto HAVING COUNT(*) > 1");
    }

    conectar con = new conectar();
    Connection cn;
    ObservableList list = FXCollections.observableArrayList("ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE");
    DecimalFormat df = new DecimalFormat();
    byte valor1 = 0, valor2 = 0, valor3 = 0;
    ResultSet rs;

    private void porcentaje() {
        int p1 = 0, p2 = 0;
        double gn;
        String da = metodos.fecha(LocalDate.now());
        try {
            cn = con.conexion();
            rs = cn.createStatement().executeQuery("SELECT SUM(total) FROM ventas WHERE fecha='" + metodos.fecha(LocalDate.now()) + "'");
            while (rs.next()) {
                p1 = rs.getInt(1);
            }
            labelP1.setText(df.format(p1));

            rs = cn.createStatement().executeQuery("SELECT SUM(total) FROM compras WHERE fecha='" + metodos.fecha(LocalDate.now()) + "'");
            while (rs.next()) {
                p2 = rs.getInt(1);
            }

            labelP2.setText(df.format(p2));
            gn = p1 - p2;
            labelPorcentaje.setText(Math.round((gn / p1) * 100) + "%");
            labelNaranja.setText(da);
            labelAzul.setText(da);
            labelRojo.setText(da);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    private void grafica(String sql) {
        list = FXCollections.observableArrayList();

        try {
            cn = con.conexion();
            rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
            }
            grafica1.setData(list);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }

    }

    private void load1(String sql, Label fecha, Label valor, String fe) {
        try {
            cn = con.conexion();
            rs = cn.createStatement().executeQuery(sql);
            while (rs.next()) {
                fecha.setText(fe);
                valor.setText(df.format(rs.getInt(1)));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            con.cerrarConexion();
        }
    }

    @FXML
    private void loadAzulDia(ActionEvent event) {
        if (valor2 == 0) {
            load1("SELECT SUM(total) FROM ventas WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha1, labelValor1, fecha1.getValue().toString());
            load1("SELECT COUNT(id_venta) FROM ventas WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha2, labelValor2, fecha1.getValue().toString());
            load1("SELECT COUNT(id_venta) FROM ventas WHERE anulada=1 AND fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha3, labelValor3, fecha1.getValue().toString());
            load1("SELECT COUNT(producto) FROM detalle_venta WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha4, labelValor4, fecha1.getValue().toString());
            load1("SELECT SUM(cantidad) FROM detalle_venta WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha5, labelValor5, fecha1.getValue().toString());
        } else {
            load1("SELECT SUM(total) FROM compras WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha1, labelValor1, fecha1.getValue().toString());
            load1("SELECT COUNT(id_compra) FROM compras WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha2, labelValor2, fecha1.getValue().toString());
            load1("SELECT COUNT(id_compra) FROM compras WHERE anulada=1 AND fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha3, labelValor3, fecha1.getValue().toString());
            load1("SELECT COUNT(producto) FROM detalle_compra WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha4, labelValor4, fecha1.getValue().toString());
            load1("SELECT SUM(cantidad) FROM detalle_compra WHERE fecha='" + metodos.fecha(fecha1.getValue()) + "'", lfecha5, labelValor5, fecha1.getValue().toString());
        }
    }

    @FXML
    private void loadAzulMes(ActionEvent event) {
        String da = comboMes.getValue();
        if (valor2 == 0) {
            load1("SELECT SUM(total) FROM ventas WHERE fecha LIKE '%" + da + "%'", lfecha1, labelValor1, da);
            load1("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE '%" + da + "%'", lfecha2, labelValor2, da);
            load1("SELECT COUNT(id_venta) FROM ventas WHERE anulada=1 AND fecha LIKE '%" + da + "%'", lfecha3, labelValor3, da);
            load1("SELECT COUNT(producto) FROM detalle_venta WHERE fecha LIKE '%" + da + "%'", lfecha4, labelValor4, da);
            load1("SELECT SUM(cantidad) FROM detalle_venta WHERE fecha LIKE '%" + da + "%'", lfecha5, labelValor5, da);
        } else {
            load1("SELECT SUM(total) FROM compras WHERE fecha LIKE '%" + da + "%'", lfecha1, labelValor1, da);
            load1("SELECT COUNT(id_compra) FROM compras WHERE fecha LIKE '%" + da + "%'", lfecha2, labelValor2, da);
            load1("SELECT COUNT(id_compra) FROM compras WHERE anulada=1 AND fecha LIKE '%" + da + "%'", lfecha3, labelValor3, da);
            load1("SELECT COUNT(producto) FROM detalle_compra WHERE fecha LIKE '%" + da + "%'", lfecha4, labelValor4, da);
            load1("SELECT SUM(cantidad) FROM detalle_compra WHERE fecha LIKE '%" + da + "%'", lfecha5, labelValor5, da);
        }
    }

    @FXML
    private void cambiarCompras(MouseEvent event) {
        if (valor2 == 0) {
            azul1.setText("Monto de Compras");
            azul2.setText("Compras Realizadas");
            azul3.setText("Compras Anuladas");
            azul4.setText("Productos Comprados");
            azul5.setText("Stock Comprado");
            labelVentas.setText("compras");
            valor2 = 1;
        } else {
            azul1.setText("Monto de Ventas");
            azul2.setText("Ventas Realizadas");
            azul3.setText("Ventas Anuladas");
            azul4.setText("Productos Vendidos");
            azul5.setText("Stock Vendido");
            labelVentas.setText("ventas");
            valor2 = 0;
        }
    }

    @FXML
    private void verFecha1(MouseEvent event) {
        if (valor1 == 0) {
            comboMes.setVisible(true);
            fecha1.setVisible(false);
            labelDia.setText("MES");
            valor1 = 1;
        } else {
            comboMes.setVisible(false);
            fecha1.setVisible(true);
            labelDia.setText("DIA");
            valor1 = 0;
        }
    }

    @FXML
    private void reestablecer1(ActionEvent event) {
        labelValor1.setText("0");
        labelValor2.setText("0");
        labelValor3.setText("0");
        labelValor4.setText("0");
        labelValor5.setText("0");
        labelVentas.setText("ventas");
        valor2 = 0;
        azul1.setText("Monto de Ventas");
        azul2.setText("Ventas Realizadas");
        azul3.setText("Ventas Anuladas");
        azul4.setText("Productos Vendidos");
        azul5.setText("Stock Vendido");
        fecha1.setVisible(true);
        comboMes.setVisible(false);
        labelDia.setText("DIA");

    }

    @FXML
    private void loadGrafica1DIA(ActionEvent event) {
        grafica("SELECT producto, COUNT( producto ) AS total FROM detalle_venta WHERE fecha='" + metodos.fecha(fecha2.getValue()) + "' GROUP BY producto HAVING COUNT(*) > 1");
    }

    @FXML
    private void loadGrafica1MES(ActionEvent event) {
        grafica("SELECT producto, COUNT( producto ) AS total FROM detalle_venta WHERE fecha LIKE '%" + comboMes2.getValue() + "%' GROUP BY producto HAVING COUNT(*) > 1");
    }

    @FXML
    private void verFecha2(MouseEvent event) {
        if (valor3 == 0) {
            comboMes2.setVisible(true);
            fecha2.setVisible(false);
            labelDia2.setText("MES");
            valor3 = 1;
        } else {
            comboMes2.setVisible(false);
            fecha2.setVisible(true);
            labelDia2.setText("DIA");
            valor3 = 0;
        }
    }

    @FXML
    private void verPanel2(ActionEvent event) {
        panel2.setVisible(true);
    }

    @FXML
    private void cerrarP2(MouseEvent event) {
        panel2.setVisible(false);
    }

    private void loadTargets(String sql,int m1,int m2,int m3,int m4,int m5,int m6) {
        String mes;
        try {
            cn = con.conexion();
            mes=metodos.mesV2(m1);
            rs = cn.createStatement().executeQuery(sql + mes + "%'");
            while (rs.next()) {
                Plabel1.setText(mes + "\n" + df.format(rs.getInt(1)));
            }

            mes = metodos.mesV2(m2);
            rs = cn.createStatement().executeQuery(sql + mes + "%'");
            while (rs.next()) {
                Plabel2.setText(mes + "\n" + df.format(rs.getInt(1)));
            }

            mes = metodos.mesV2(m3);
            rs = cn.createStatement().executeQuery(sql + mes + "%'");
            while (rs.next()) {
                Plabel3.setText(mes + "\n" + df.format(rs.getInt(1)));
            }

            mes = metodos.mesV2(m4);
            rs = cn.createStatement().executeQuery(sql + mes + "%'");
            while (rs.next()) {
                Plabel4.setText(mes + "\n" + df.format(rs.getInt(1)));
            }

            mes = metodos.mesV2(m5);
            rs = cn.createStatement().executeQuery(sql + mes + "%'");
            while (rs.next()) {
                Plabel5.setText(mes + "\n" + df.format(rs.getInt(1)));
            }

            mes = metodos.mesV2(m6);
            rs = cn.createStatement().executeQuery(sql + mes + "%'");
            while (rs.next()) {
                Plabel6.setText(mes + "\n" + df.format(rs.getInt(1)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(EstadisticasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cargarTarjetas(ActionEvent event) {
        switch(comboP2.getValue()){
            case "VENTAS REALIZADAS":
                loadTargets("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
            break;    
            
            case "MONTO DE VENTAS":
                loadTargets("SELECT SUM(total) FROM ventas WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
            break;
            
            //CASOS DE LAS COMPRAS
            case "COMPRAS REALIZADAS":
                loadTargets("SELECT COUNT(id_compra) FROM compras WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
            break;
            
            case "MONTO DE COMPRAS":
                loadTargets("SELECT SUM(total) FROM compras WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
            break;
        
        }
        flechaAdelante.setVisible(true);
        flechaAtras.setVisible(false);
    }

    @FXML
    private void anteriorPagina(MouseEvent event) {
        switch (comboP2.getValue()) {
            case "VENTAS REALIZADAS":
                loadTargets("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
                break;
            case "MONTO DE VENTAS":
                loadTargets("SELECT SUM(total) FROM ventas WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
                break;
            case "COMPRAS REALIZADAS":
                loadTargets("SELECT COUNT(id_compra) FROM compras WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
                break;
            case "MONTO DE COMPRAS":
                loadTargets("SELECT SUM(total) FROM compras WHERE fecha LIKE'%", 1, 2, 3, 4, 5, 6);
                break;
            default:
                break;
        }
        flechaAdelante.setVisible(true);
        flechaAtras.setVisible(false);
    }

    @FXML
    private void siguientePagina(MouseEvent event) {
        switch (comboP2.getValue()) {
            case "VENTAS REALIZADAS":
                loadTargets("SELECT COUNT(id_venta) FROM ventas WHERE fecha LIKE'%", 7, 8, 9, 10, 11, 12);
                break;
            case "MONTO DE VENTAS":
                loadTargets("SELECT SUM(total) FROM ventas WHERE fecha LIKE'%", 7, 8, 9, 10, 11, 12);
                break;
            case "COMPRAS REALIZADAS":
                loadTargets("SELECT COUNT(id_compra) FROM compras WHERE fecha LIKE'%", 7, 8, 9, 10, 11, 12);
                break;
            case "MONTO DE COMPRAS":
                loadTargets("SELECT SUM(total) FROM compras WHERE fecha LIKE'%", 7, 8, 9, 10, 11, 12);
                break;
            default:
                break;
        }
        flechaAdelante.setVisible(false);
        flechaAtras.setVisible(true);
    }

}
