import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private Connection connection;
    private String ultimoError = "";

    private Connection obtenerConexion() {
        connection = Conexion.getConnection();
        return connection;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public boolean insertar(Cliente cliente) {
        ultimoError = "";

        String sql = "INSERT INTO clientes "
                + "(codigo_cliente, dui, nombre_completo, telefono, correo_electronico, direccion, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = obtenerConexion()) {
            if (con == null)
                return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, cliente.getCodigoCliente());
                ps.setString(2, cliente.getDui());
                ps.setString(3, cliente.getNombreCompleto());
                ps.setString(4, cliente.getTelefono());
                ps.setString(5, cliente.getCorreoElectronico());
                ps.setString(6, cliente.getDireccion());
                ps.setString(7, cliente.getEstado());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            mostrarErrorSQL(e);
            return false;
        }
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id_cliente";

        try (Connection con = obtenerConexion()) {
            if (con == null)
                return lista;

            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizar(Cliente cliente) {
        ultimoError = "";

        String sql = "UPDATE clientes SET "
                + "codigo_cliente = ?, dui = ?, nombre_completo = ?, telefono = ?, "
                + "correo_electronico = ?, direccion = ?, estado = ? "
                + "WHERE id_cliente = ?";

        try (Connection con = obtenerConexion()) {
            if (con == null)
                return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, cliente.getCodigoCliente());
                ps.setString(2, cliente.getDui());
                ps.setString(3, cliente.getNombreCompleto());
                ps.setString(4, cliente.getTelefono());
                ps.setString(5, cliente.getCorreoElectronico());
                ps.setString(6, cliente.getDireccion());
                ps.setString(7, cliente.getEstado());
                ps.setInt(8, cliente.getIdCliente());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            mostrarErrorSQL(e);
            return false;
        }
    }

    public boolean eliminar(int idCliente) {
        ultimoError = "";

        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection con = obtenerConexion()) {
            if (con == null)
                return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            ultimoError = "No se pudo eliminar el cliente.";
            System.err.println(
                    "Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente buscar(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection con = obtenerConexion()) {
            if (con == null)
                return null;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println(
                    "Error al buscar cliente: " + e.getMessage());
        }

        return null;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setCodigoCliente(rs.getString("codigo_cliente"));
        cliente.setDui(rs.getString("dui"));
        cliente.setNombreCompleto(rs.getString("nombre_completo"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreoElectronico(rs.getString("correo_electronico"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setEstado(rs.getString("estado"));

        return cliente;
    }

    private void mostrarErrorSQL(SQLException e) {

        if (e.getErrorCode() == 1062) {

            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.contains("codigo_cliente")) {
                ultimoError = "El código del cliente ya está registrado.";

            } else if (mensaje != null && mensaje.contains("dui")) {
                ultimoError = "El DUI ya está registrado.";

            } else {
                ultimoError = "El código o DUI ya están registrados.";
            }

            System.err.println("Error: " + ultimoError);

        } else {
            ultimoError = "Ocurrió un error al guardar los datos.";

            System.err.println(
                    "Error SQL (" + e.getErrorCode() + "): "
                            + e.getMessage());
        }
    }
}