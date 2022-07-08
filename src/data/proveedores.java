
package data;

public class proveedores {
    
    int id;
    String proveedor;
    long telefono;
    String residencia,correo,adicional;

    public proveedores(int id, String proveedor, long telefono, String residencia, String correo, String adicional) {
        this.id = id;
        this.proveedor = proveedor;
        this.telefono = telefono;
        this.residencia = residencia;
        this.correo = correo;
        this.adicional = adicional;
    }

    public int getId() {
        return id;
    }

    public String getProveedor() {
        return proveedor;
    }

    public long getTelefono() {
        return telefono;
    }

    public String getResidencia() {
        return residencia;
    }

    public String getCorreo() {
        return correo;
    }

    public String getAdicional() {
        return adicional;
    }
    
    
    
}
