import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class CuentaDAO {

    private Connection obtenerConexion() {
        return Conexion.getConnection();
    }

    public List<Cuenta> listarPorCliente(int idCliente) {
        List<Cuenta> lista = new ArrayList<>();

        String sql = "SELECT * FROM cuentas "
                + "WHERE id_cliente = ? "
                + "ORDER BY id_cuenta";

        try (Connection con = obtenerConexion()) {
            if (con == null) {
                System.err.println(
                        "Error al listar cuentas: no hay conexión con MySQL.");
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, idCliente);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Cuenta cuenta = new Cuenta();

                        cuenta.setIdCuenta(
                                rs.getInt("id_cuenta"));

                        cuenta.setNumeroCuenta(
                                rs.getString("numero_cuenta"));

                        cuenta.setIdCliente(
                                rs.getInt("id_cliente"));

                        cuenta.setSaldo(
                                rs.getDouble("saldo"));

                        cuenta.setEstado(
                                rs.getString("estado"));

                        lista.add(cuenta);
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al listar cuentas: "
                            + e.getMessage());
        }

        return lista;
    }

    public boolean depositar(
            int idCuenta,
            double monto) {
        if (!Double.isFinite(monto) || monto <= 0) {
            return false;
        }

        String actualizar = "UPDATE cuentas SET saldo = saldo + ? "
                + "WHERE id_cuenta = ? "
                + "AND estado = 'ACTIVA'";

        String registrar = "INSERT INTO transacciones "
                + "(id_cuenta, tipo, monto, cuenta_relacionada) "
                + "VALUES (?, 'DEPOSITO', ?, NULL)";

        try (Connection con = obtenerConexion()) {
            if (con == null) {
                return false;
            }

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(actualizar)) {

                ps.setDouble(1, monto);
                ps.setInt(2, idCuenta);

                if (ps.executeUpdate() == 0) {

                    con.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(registrar)) {

                ps.setInt(1, idCuenta);
                ps.setDouble(2, monto);

                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Error al depositar: "
                            + e.getMessage());

            return false;
        }
    }

    public boolean retirar(
            int idCuenta,
            double monto) {
        if (!Double.isFinite(monto) || monto <= 0) {
            return false;
        }

        String actualizar = "UPDATE cuentas c "
                + "INNER JOIN clientes cl "
                + "ON c.id_cliente = cl.id_cliente "
                + "SET c.saldo = c.saldo - ? "
                + "WHERE c.id_cuenta = ? "
                + "AND c.estado = 'ACTIVA' "
                + "AND cl.estado = 'Activo' "
                + "AND c.saldo >= ?";

        String registrar = "INSERT INTO transacciones "
                + "(id_cuenta, tipo, monto, cuenta_relacionada) "
                + "VALUES (?, 'RETIRO', ?, NULL)";

        try (Connection con = obtenerConexion()) {
            if (con == null) {
                return false;
            }

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(actualizar)) {

                ps.setDouble(1, monto);
                ps.setInt(2, idCuenta);
                ps.setDouble(3, monto);

                if (ps.executeUpdate() == 0) {

                    con.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(registrar)) {

                ps.setInt(1, idCuenta);
                ps.setDouble(2, monto);

                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Error al retirar: "
                            + e.getMessage());

            return false;
        }
    }
}