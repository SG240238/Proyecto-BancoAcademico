class Cuenta {

    private int idCuenta;
    private String numeroCuenta;
    private int idCliente;
    private double saldo;
    private String estado;

    public Cuenta() {
    }

    public Cuenta(int idCuenta, String numeroCuenta, int idCliente,
            double saldo, String estado) {
        this.idCuenta = idCuenta;
        this.numeroCuenta = numeroCuenta;
        this.idCliente = idCliente;
        this.saldo = saldo;
        this.estado = estado;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}