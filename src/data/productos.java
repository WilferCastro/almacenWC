
package data;

public class productos {
    
    int id,stock;
    String codigo,producto,categoria,descripcion,precioVenta,precioCompra,idCategoria;

    public productos(int id, String precioVenta, String precioCompra, int stock, String codigo, String producto, String categoria, String descripcion,String idCategoria) {
        this.id = id;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
        this.stock = stock;
        this.codigo = codigo;
        this.producto = producto;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
    }

    public int getId() {
        return id;
    }

    public String getPrecioVenta() {
        return precioVenta;
    }

    public String getPrecioCompra() {
        return precioCompra;
    }

    public int getStock() {
        return stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIdCategoria() {
        return idCategoria;
    }
    
    
    
    
    
}
