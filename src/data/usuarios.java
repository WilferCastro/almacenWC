
package data;

public class usuarios {
    
    int id;
    String nombre;
    String usuario;
    String cargo;
    long documento;
    String residencia;
    long telefono;

    public usuarios(int id, String nombre, String usuario, String cargo, long documento, String residencia, long telefono) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.cargo = cargo;
        this.documento = documento;
        this.residencia = residencia;
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getCargo() {
        return cargo;
    }

    public long getDocumento() {
        return documento;
    }

    public String getResidencia() {
        return residencia;
    }

    public long getTelefono() {
        return telefono;
    }
    
    
    
}
