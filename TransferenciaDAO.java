import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class TransferenciaDAO {

    private static final String SQL_BUSCAR_CUENTA =
            "SELECT id_cuenta, saldo, estado FROM cuentas WHERE numero_cuenta = ? FOR UPDATE";

    private static final String SQL_DEBITAR =
            "UPDATE cuentas SET saldo = saldo - ? WHERE id_cuenta = ?";

    private static final String SQL_ACREDITAR =
            "UPDATE cuentas SET saldo = saldo + ? WHERE id_cuenta = ?";

    private static final String SQL_INSERTAR_TRANSACCION =
            "INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada) VALUES (?, ?, ?, ?)";

    private static final String SQL_OBTENER_MOVIMIENTOS =
            "SELECT t.fecha, t.tipo, t.monto, cr.numero_cuenta AS cuenta_relacionada " +
            "FROM transacciones t " +
            "JOIN cuentas c  ON t.id_cuenta = c.id_cuenta " +
            "LEFT JOIN cuentas cr ON t.cuenta_relacionada = cr.id_cuenta " +
            "WHERE c.numero_cuenta = ? " +
            "ORDER BY t.fecha DESC";

    public void transferir(String numOrigen, String numDestino, BigDecimal monto)
            throws SQLException {

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (numOrigen == null || numDestino == null || numOrigen.trim().equalsIgnoreCase(numDestino.trim())) {
            throw new IllegalArgumentException(
                    "La cuenta origen y la cuenta destino no pueden ser la misma.");
        }

        try (Connection con = Conexion.getConnection()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo establecer conexión con la base de datos.");
            }

            con.setAutoCommit(false);

            try {
                int idOrigen, idDestino;
                BigDecimal saldoOrigen;
                String estadoOrigen, estadoDestino;

                try (PreparedStatement psOrigen = con.prepareStatement(SQL_BUSCAR_CUENTA)) {
                    psOrigen.setString(1, numOrigen.trim());
                    try (ResultSet rsOrigen = psOrigen.executeQuery()) {
                        if (!rsOrigen.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta origen \"" + numOrigen + "\" no existe.");
                        }
                        idOrigen     = rsOrigen.getInt("id_cuenta");
                        saldoOrigen  = rsOrigen.getBigDecimal("saldo");
                        estadoOrigen = rsOrigen.getString("estado");
                    }
                }

                try (PreparedStatement psDestino = con.prepareStatement(SQL_BUSCAR_CUENTA)) {
                    psDestino.setString(1, numDestino.trim());
                    try (ResultSet rsDestino = psDestino.executeQuery()) {
                        if (!rsDestino.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta destino \"" + numDestino + "\" no existe.");
                        }
                        idDestino     = rsDestino.getInt("id_cuenta");
                        estadoDestino = rsDestino.getString("estado");
                    }
                }

                if (!"ACTIVA".equals(estadoOrigen)) {
                    throw new IllegalArgumentException(
                            "La cuenta origen \"" + numOrigen + "\" no está activa (estado: " + estadoOrigen + ").");
                }

                if (!"ACTIVA".equals(estadoDestino)) {
                    throw new IllegalArgumentException(
                            "La cuenta destino \"" + numDestino + "\" no está activa (estado: " + estadoDestino + ").");
                }

                if (saldoOrigen.compareTo(monto) < 0) {
                    throw new IllegalArgumentException(
                            "Saldo insuficiente en la cuenta origen. " +
                            "Saldo disponible: " + saldoOrigen + ", monto solicitado: " + monto + ".");
                }

                try (PreparedStatement psDebitar = con.prepareStatement(SQL_DEBITAR)) {
                    psDebitar.setBigDecimal(1, monto);
                    psDebitar.setInt(2, idOrigen);
                    psDebitar.executeUpdate();
                }

                try (PreparedStatement psAcreditar = con.prepareStatement(SQL_ACREDITAR)) {
                    psAcreditar.setBigDecimal(1, monto);
                    psAcreditar.setInt(2, idDestino);
                    psAcreditar.executeUpdate();
                }

                try (PreparedStatement psTxOrigen = con.prepareStatement(SQL_INSERTAR_TRANSACCION)) {
                    psTxOrigen.setInt(1, idOrigen);
                    psTxOrigen.setString(2, "TRANSFERENCIA_ENVIADA");
                    psTxOrigen.setBigDecimal(3, monto);
                    psTxOrigen.setInt(4, idDestino);
                    psTxOrigen.executeUpdate();
                }

                try (PreparedStatement psTxDestino = con.prepareStatement(SQL_INSERTAR_TRANSACCION)) {
                    psTxDestino.setInt(1, idDestino);
                    psTxDestino.setString(2, "TRANSFERENCIA_RECIBIDA");
                    psTxDestino.setBigDecimal(3, monto);
                    psTxDestino.setInt(4, idOrigen);
                    psTxDestino.executeUpdate();
                }

                con.commit();

            } catch (IllegalArgumentException e) {
                con.rollback();
                throw e;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public DefaultTableModel obtenerMovimientos(String numCuenta) throws SQLException {

        String[] columnas = {"Fecha", "Tipo", "Monto", "Cuenta relacionada"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_OBTENER_MOVIMIENTOS)) {

            ps.setString(1, numCuenta.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fecha             = rs.getString("fecha");
                    String tipo              = rs.getString("tipo");
                    BigDecimal montoFila     = rs.getBigDecimal("monto");
                    String cuentaRelacionada = rs.getString("cuenta_relacionada");

                    if (cuentaRelacionada == null) {
                        cuentaRelacionada = "-";
                    }

                    modelo.addRow(new Object[]{fecha, tipo, montoFila, cuentaRelacionada});
                }
            }
        }

        return modelo;
    }
}
