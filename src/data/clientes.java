package data;

import java.sql.Date;

public class clientes {

    private int idCliente;
    private String cliente, correo, residencia, adicional;
    private Date fecha;
    private long documento, telefono;

    public clientes(int idCliente, String cliente, String correo, String residencia, String adicional, Date fecha, long documento, long telefono) {
        this.idCliente = idCliente;
        this.cliente = cliente;
        this.correo = correo;
        this.residencia = residencia;
        this.adicional = adicional;
        this.fecha = fecha;
        this.documento = documento;
        this.telefono = telefono;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getCliente() {
        return cliente;
    }

    public String getCorreo() {
        return correo;
    }

    public String getResidencia() {
        return residencia;
    }

    public String getAdicional() {
        return adicional;
    }

    public Date getFecha() {
        return fecha;
    }

    public long getDocumento() {
        return documento;
    }

    public long getTelefono() {
        return telefono;
    }

    

    


    
}
