package data;

import com.jfoenix.controls.JFXComboBox;
import controllers.VentasController;
import java.io.UnsupportedEncodingException;
import static java.lang.Integer.min;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.Math.round;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Base64;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;

public class metodos {

    //PRIMERA LETRA EN MAYUSCULA
    public static String line(String nombre) {
        if (nombre.equals("")) {
            return "";
        }
        char[] caracteres = nombre.toCharArray();

        caracteres[0] = Character.toUpperCase(caracteres[0]);

        for (int i = 0; i < nombre.length() - 2; i++) {
            if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ',') {
                caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
            }
        }
        return new String(caracteres);
    }

    //ENCRIPTAR CONTRASEÑA
    public static String bcrypt(String cadena) {
        return Base64.getEncoder().encodeToString(cadena.getBytes());
    }

    //DESENCRIPTAR CONTRASEÑA
    public static String dcrypt(String cadena) throws UnsupportedEncodingException {
        byte decode[] = Base64.getDecoder().decode(cadena.getBytes());
        return new String(decode, "utf-8");
    }
    
    
    private static int numeroAleatorioEnRango(int minimo, int maximo) {
        // nextInt regresa en rango pero con límite superior exclusivo, por eso sumamos 1
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }

    //GENERAR CADENA ALEATORIA
    public static String cadenaAleatoria(int longitud) {
        // El banco de caracteres
        String banco = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        // La cadena en donde iremos agregando un carácter aleatorio
        String cadena = "";
        for (int x = 0; x < longitud; x++) {
            int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
            char caracterAleatorio = banco.charAt(indiceAleatorio);
            cadena += caracterAleatorio;
        }
        return cadena;
    }
    
    //FECHA EN ESPAÑOL Y FORMATO
    public static String fecha(LocalDate l) {
        Month mes = l.getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
        String date=nombre+"/"+l.getDayOfMonth()+"/"+l.getYear();
        return date;
    }
    
    public static String mes(LocalDate l) {
        Month mes = l.getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
        return nombre;
    }
    
    public static String mesV2(int mes){
        
        String nuevo="";
        switch (mes){
            case 1:
                nuevo="ENERO";
                break;
            case 2:
                nuevo="FEBRERO";
                break;    
            case 3:
                nuevo="MARZO";
                break;
            case 4:
                nuevo="ABRIL";
                break;
            case 5:
                nuevo="MAYO";
                break;
            case 6:
                nuevo="JUNIO";
                break;    
            case 7:
                nuevo="JULIO";
                break;  
            case 8:
                nuevo="AGOSTO";
                break;  
            case 9:
                nuevo="SEPTIEMBRE";
                break;  
            case 10:
                nuevo="OCTUBRE";
                break;  
            case 11:
                nuevo="NOVIEMBRE";
                break;      
            case 12:
                nuevo="DICIEMBRE";
                break;  
        }
        
        return nuevo;
    }
    

}
