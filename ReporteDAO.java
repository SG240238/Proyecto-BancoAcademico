import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class ReporteDAO {

    private static final String SQL_HISTORIAL =
            "SELECT t.fecha, c.numero_cuenta, t.tipo, t.monto, " +
            "COALESCE(cr.numero_cuenta, '-') AS cuenta_relacionada " +
            "FROM transacciones t " +
            "INNER JOIN cuentas c ON t.id_cuenta = c.id_cuenta " +
            "LEFT JOIN cuentas cr ON t.cuenta_relacionada = cr.id_cuenta " +
            "ORDER BY t.fecha DESC, t.id_transaccion DESC";

    private static final String SQL_HISTORIAL_POR_CUENTA =
            "SELECT t.fecha, c.numero_cuenta, t.tipo, t.monto, " +
            "COALESCE(cr.numero_cuenta, '-') AS cuenta_relacionada " +
            "FROM transacciones t " +
            "INNER JOIN cuentas c ON t.id_cuenta = c.id_cuenta " +
            "LEFT JOIN cuentas cr ON t.cuenta_relacionada = cr.id_cuenta " +
            "WHERE c.numero_cuenta = ? " +
            "ORDER BY t.fecha DESC, t.id_transaccion DESC";

    private DefaultTableModel crearModelo(final String... encabezados) {
        return new DefaultTableModel(encabezados, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private DefaultTableModel reporteCuentasPorEstado(String estado)
            throws SQLException {
        DefaultTableModel modelo = crearModelo(
                "Número de cuenta", "Cliente", "Saldo", "Estado");

        String sql =
                "SELECT ca.numero_cuenta, " +
                "CONCAT(c.nombre, ' ', c.apellido) AS cliente, " +
                "ca.saldo, ca.estado " +
                "FROM cuentas ca " +
                "INNER JOIN clientes c ON ca.id_cliente = c.id_cliente " +
                "WHERE ca.estado = ? " +
                "ORDER BY ca.numero_cuenta";

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, estado);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    modelo.addRow(new Object[] {
                        resultado.getString("numero_cuenta"),
                        resultado.getString("cliente"),
                        resultado.getBigDecimal("saldo"),
                        resultado.getString("estado")
                    });
                }
            }
        }

        return modelo;
    }

    public DefaultTableModel reporteCuentasActivas() throws SQLException {
        return reporteCuentasPorEstado("ACTIVA");
    }

    public DefaultTableModel reporteCuentasBloqueadas() throws SQLException {
        return reporteCuentasPorEstado("BLOQUEADA");
    }

    public DefaultTableModel reporteClientesRegistrados() throws SQLException {
        DefaultTableModel modelo = crearModelo(
                "ID cliente", "Nombre", "Apellido", "DUI", "Teléfono", "Correo");
        String sql =
                "SELECT id_cliente, nombre, apellido, dui, telefono, correo " +
                "FROM clientes ORDER BY id_cliente";

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                modelo.addRow(new Object[] {
                    resultado.getInt("id_cliente"),
                    resultado.getString("nombre"),
                    resultado.getString("apellido"),
                    resultado.getString("dui"),
                    resultado.getString("telefono"),
                    resultado.getString("correo")
                });
            }
        }

        return modelo;
    }

    public DefaultTableModel reporteHistorialTransacciones() throws SQLException {
        return ejecutarReporteTransacciones(SQL_HISTORIAL, null);
    }

    public DefaultTableModel reporteTransaccionesPorCuenta(String numCuenta)
            throws SQLException {
        return ejecutarReporteTransacciones(SQL_HISTORIAL_POR_CUENTA, numCuenta);
    }

    private DefaultTableModel ejecutarReporteTransacciones(
            String sql, String numCuenta) throws SQLException {
        DefaultTableModel modelo = crearModelo(
                "Fecha",
                "Número de cuenta",
                "Tipo",
                "Monto",
                "Cuenta relacionada");

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            if (numCuenta != null) {
                sentencia.setString(1, numCuenta);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    modelo.addRow(new Object[] {
                        resultado.getTimestamp("fecha"),
                        resultado.getString("numero_cuenta"),
                        resultado.getString("tipo"),
                        resultado.getBigDecimal("monto"),
                        resultado.getString("cuenta_relacionada")
                    });
                }
            }
        }

        return modelo;
    }

    public BigDecimal totalDepositado() throws SQLException {
        return obtenerTotal("DEPOSITO");
    }

    public BigDecimal totalRetirado() throws SQLException {
        return obtenerTotal("RETIRO");
    }

    public BigDecimal totalTransferido() throws SQLException {
        // Solo se suman las filas enviadas: la fila recibida no se duplica.
        return obtenerTotal("TRANSFERENCIA_ENVIADA");
    }

    private BigDecimal obtenerTotal(String tipo) throws SQLException {
        String sql =
                "SELECT COALESCE(SUM(monto), 0) AS total " +
                "FROM transacciones WHERE tipo = ?";

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, tipo);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getBigDecimal("total");
                }
            }
        }

        throw new SQLException("La consulta del total no devolvió resultados.");
    }
}
