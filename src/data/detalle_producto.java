
package data;

public class detalle_producto {
    
    int id_producto;
    String codigo,producto,cantidad,precio,total;

    public detalle_producto(int id_producto, String codigo, String producto, String cantidad, String precio, String total) {
        this.id_producto = id_producto;
        this.codigo = codigo;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
    }

    public int getId_producto() {
        return id_producto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public String getTotal() {
        return total;
    }
    
    
}
