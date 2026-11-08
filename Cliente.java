/**
 * Clase modelo que representa un registro de la tabla "clientes"
 * de la base de datos banco_academico_db.
 *
 * Los campos coinciden exactamente con las columnas de la tabla:
 * id_cliente, codigo_cliente, dui, nombre_completo, telefono,
 * correo_electronico, direccion y estado.
 */
public class Cliente {

    // ===================== CAMPOS =====================
    private int idCliente;
    private String codigoCliente;
    private String dui;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private String direccion;
    private String estado;

    // ===================== CONSTRUCTORES =====================

    /** Constructor vacío. */
    public Cliente() {
    }

   
    public Cliente(int idCliente, String codigoCliente, String dui, String nombreCompleto,
                   String telefono, String correoElectronico, String direccion, String estado) {
        this.idCliente = idCliente;
        this.codigoCliente = codigoCliente;
        this.dui = dui;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.direccion = direccion;
        this.estado = estado;
    }

    // ===================== GETTERS Y SETTERS =====================

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
