package data;

public class detalles {

    String codigo, producto, precio;
    int cantidad, total, stock, idProducto;

    public detalles(String codigo, String producto, String precio, int cantidad, int total, int stock, int idProducto) {
        this.codigo = codigo;
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.total = total;
        this.stock = stock;
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public String getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getTotal() {
        return total;
    }

    public int getStock() {
        return stock;
    }

    public int getIdProducto() {
        return idProducto;
    }

}
