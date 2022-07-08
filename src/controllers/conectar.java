package controllers;

//import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class conectar {

    private Connection conectar = null;

    public Connection conexion() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conectar = DriverManager.getConnection("jdbc:mysql://localhost/store", "root", "");//localhost:

        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return conectar;

    }

    //CERRAR LA CONEXION CON EL SERVIDOR
    public void cerrarConexion() {
        try {
            if(conectar!=null){
            conectar.close();
            }
        } catch (SQLException sqle) {
            System.out.print("Ha ocurrido un error al intentar cerrar la conexion. Error:" + sqle.getMessage());
        }
    }

}


/*//OTRO FRAME
conectar con=new conectar();
Connection cn=con.conexion();
 */
