
package data;

public class ventas {
    
    int id;
    String cliente,vendedor,fecha,subtotal,total,descuento,domicilio;

    public ventas(int id, String cliente, String vendedor, String fecha, String subtotal, String total, String descuento, String domicilio) {
        this.id = id;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.total = total;
        this.descuento = descuento;
        this.domicilio = domicilio;
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getVendedor() {
        return vendedor;
    }

    public String getFecha() {
        return fecha;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getTotal() {
        return total;
    }

    public String getDescuento() {
        return descuento;
    }

    public String getDomicilio() {
        return domicilio;
    }
    
    
}
