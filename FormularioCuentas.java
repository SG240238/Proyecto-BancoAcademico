
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormularioCuentas extends JFrame {

    private JTable tblCuentas;
    private DefaultTableModel modeloTabla;
    private CuentaDAO cuentaDAO;

    private int idCliente;
    private String estadoCliente;

    private JButton btnRetirar;

    public FormularioCuentas(
            int idCliente,
            String nombreCliente,
            String estadoCliente) {

        this.idCliente = idCliente;
        this.estadoCliente = estadoCliente;

        cuentaDAO = new CuentaDAO();

        setTitle("Cuentas de " + nombreCliente);
        setSize(720, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        initComponents(nombreCliente);
        cargarCuentas();
    }

    private void initComponents(
            String nombreCliente) {

        JPanel principal = new JPanel(
                new BorderLayout(10, 10));

        principal.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "Cuentas de " + nombreCliente);

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24));

        principal.add(
                titulo,
                BorderLayout.NORTH);

        String[] columnas = {
                "ID",
                "Número de cuenta",
                "Saldo",
                "Estado"
        };

        modeloTabla = new DefaultTableModel(
                columnas,
                0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        tblCuentas = new JTable(modeloTabla);

        tblCuentas.setRowHeight(28);

        tblCuentas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(
                tblCuentas);

        principal.add(
                scroll,
                BorderLayout.CENTER);

        JPanel botones = new JPanel(
                new GridLayout(
                        1,
                        3,
                        10,
                        0));

        JButton btnDepositar = new JButton("Depositar");

        btnRetirar = new JButton("Retirar");

        JButton btnCerrar = new JButton("Cerrar");

        btnDepositar.addActionListener(
                e -> depositar());

        btnRetirar.addActionListener(
                e -> retirar());

        btnCerrar.addActionListener(
                e -> dispose());

        botones.add(btnDepositar);
        botones.add(btnRetirar);
        botones.add(btnCerrar);

        principal.add(
                botones,
                BorderLayout.SOUTH);

        add(principal);

        // Si el cliente está inactivo,
        // se deshabilita el botón Retirar.
        if (!estadoCliente.equalsIgnoreCase("Activo")) {

            btnRetirar.setEnabled(false);

            btnRetirar.setToolTipText(
                    "El cliente está inactivo y no puede realizar retiros.");
        }
    }

    private void cargarCuentas() {

        modeloTabla.setRowCount(0);

        List<Cuenta> cuentas = cuentaDAO.listarPorCliente(
                idCliente);

        for (Cuenta cuenta : cuentas) {

            modeloTabla.addRow(
                    new Object[] {

                            cuenta.getIdCuenta(),

                            cuenta.getNumeroCuenta(),

                            String.format(
                                    "$%.2f",
                                    cuenta.getSaldo()),

                            cuenta.getEstado()
                    });
        }
    }

    private void depositar() {

        int fila = tblCuentas.getSelectedRow();

        if (fila == -1) {

            mostrarError(
                    "Seleccione una cuenta.");

            return;
        }

        double monto = pedirMonto();

        if (monto <= 0) {
            return;
        }

        int idCuenta = Integer.parseInt(
                modeloTabla
                        .getValueAt(
                                fila,
                                0)
                        .toString());

        if (cuentaDAO.depositar(
                idCuenta,
                monto)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Depósito realizado correctamente.\n"
                            + "Monto: $"
                            + String.format(
                                    "%.2f",
                                    monto));

            cargarCuentas();

        } else {

            mostrarError(
                    "No se pudo realizar el depósito.\n"
                            + "Verifique que la cuenta esté activa.");
        }
    }

    private void retirar() {

        int fila = tblCuentas.getSelectedRow();

        if (fila == -1) {

            mostrarError(
                    "Seleccione una cuenta.");

            return;
        }

        // Verificar primero el estado del cliente.
        if (!estadoCliente.equalsIgnoreCase("Activo")) {

            mostrarError(
                    "El cliente está inactivo.\n"
                            + "No puede realizar retiros.");

            return;
        }

        // Verificar el estado de la cuenta.
        String estadoCuenta = modeloTabla
                .getValueAt(
                        fila,
                        3)
                .toString();

        if (!estadoCuenta.equalsIgnoreCase("ACTIVA")) {

            mostrarError(
                    "La cuenta está inactiva.\n"
                            + "No puede realizar retiros.");

            return;
        }

        double monto = pedirMonto();

        if (monto <= 0) {
            return;
        }

        int idCuenta = Integer.parseInt(
                modeloTabla
                        .getValueAt(
                                fila,
                                0)
                        .toString());

        if (cuentaDAO.retirar(
                idCuenta,
                monto)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Retiro realizado correctamente.\n"
                            + "Monto: $"
                            + String.format(
                                    "%.2f",
                                    monto));

            cargarCuentas();

        } else {

            mostrarError(
                    "No se pudo realizar el retiro.\n"
                            + "Verifique que la cuenta tenga saldo suficiente.");
        }
    }

    private double pedirMonto() {

        String texto = JOptionPane.showInputDialog(
                this,
                "Ingrese el monto:",
                "Operación",
                JOptionPane.PLAIN_MESSAGE);

        if (texto == null) {
            return -1;
        }

        try {

            double monto = Double.parseDouble(texto);

            if (monto <= 0) {

                mostrarError(
                        "El monto debe ser mayor que cero.");

                return -1;
            }

            return monto;

        } catch (NumberFormatException e) {

            mostrarError(
                    "Ingrese un monto válido.");

            return -1;
        }
    }

    private void mostrarError(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.WARNING_MESSAGE);
    }
}
