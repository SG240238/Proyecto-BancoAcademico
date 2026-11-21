import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FormularioReportes extends JFrame {

    private static final String REPORTE_CUENTAS_ACTIVAS = "Cuentas activas";
    private static final String REPORTE_CUENTAS_BLOQUEADAS = "Cuentas bloqueadas";
    private static final String REPORTE_CLIENTES = "Clientes registrados";
    private static final String REPORTE_HISTORIAL = "Historial de transacciones";
    private static final String REPORTE_POR_CUENTA = "Transacciones por cuenta";
    private static final String REPORTE_DEPOSITADO = "Total depositado";
    private static final String REPORTE_RETIRADO = "Total retirado";
    private static final String REPORTE_TRANSFERIDO = "Total transferido";

    private static final String VISTA_LISTA = "lista";
    private static final String VISTA_TOTAL = "total";

    private final JComboBox<String> selectorReportes;
    private final JTextField campoNumeroCuenta;
    private final JTable tabla;
    private final JLabel etiquetaCantidad;
    private final JLabel etiquetaTotal;
    private final JPanel panelVistas;
    private final CardLayout administradorVistas;
    private final ReporteDAO reporteDAO;

    public FormularioReportes() {
        super("Reportes - Banco Académico");

        reporteDAO = new ReporteDAO();
        selectorReportes = new JComboBox<String>(new String[] {
            REPORTE_CUENTAS_ACTIVAS,
            REPORTE_CUENTAS_BLOQUEADAS,
            REPORTE_CLIENTES,
            REPORTE_HISTORIAL,
            REPORTE_POR_CUENTA,
            REPORTE_DEPOSITADO,
            REPORTE_RETIRADO,
            REPORTE_TRANSFERIDO
        });

        campoNumeroCuenta = new JTextField(16);
        campoNumeroCuenta.setEnabled(false);

        tabla = new JTable();
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);

        JScrollPane desplazamientoTabla = new JScrollPane(tabla);
        desplazamientoTabla.setPreferredSize(new Dimension(760, 360));

        etiquetaCantidad = new JLabel("Registros encontrados: 0");
        etiquetaCantidad.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 2));

        etiquetaTotal = new JLabel("", SwingConstants.CENTER);
        etiquetaTotal.setFont(new Font("SansSerif", Font.BOLD, 32));
        etiquetaTotal.setForeground(new Color(42, 91, 73));

        administradorVistas = new CardLayout();
        panelVistas = new JPanel(administradorVistas);

        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.add(desplazamientoTabla, BorderLayout.CENTER);

        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(Color.WHITE);
        panelTotal.add(etiquetaTotal, BorderLayout.CENTER);

        panelVistas.add(panelLista, VISTA_LISTA);
        panelVistas.add(panelTotal, VISTA_TOTAL);

        construirInterfaz();
        selectorReportes.addActionListener(evento -> actualizarCampoCuenta());
        actualizarCampoCuenta();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 480));
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        Color fondo = new Color(245, 248, 247);
        Color fondoPanel = new Color(232, 241, 237);

        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        contenido.setBackground(fondo);

        JPanel panelControles = new JPanel(new GridBagLayout());
        panelControles.setBackground(fondoPanel);
        panelControles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 224, 218)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(5, 6, 5, 6);
        restricciones.anchor = GridBagConstraints.WEST;

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        panelControles.add(new JLabel("Reporte:"), restricciones);

        restricciones.gridx = 1;
        restricciones.weightx = 1;
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(selectorReportes, restricciones);

        restricciones.gridx = 2;
        restricciones.weightx = 0;
        restricciones.fill = GridBagConstraints.NONE;
        panelControles.add(new JLabel("Número de cuenta:"), restricciones);

        restricciones.gridx = 3;
        restricciones.weightx = 0.5;
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(campoNumeroCuenta, restricciones);

        JButton botonGenerar = new JButton("Generar reporte");
        botonGenerar.addActionListener(evento -> generarReporte());

        restricciones.gridx = 4;
        restricciones.weightx = 0;
        restricciones.fill = GridBagConstraints.NONE;
        panelControles.add(botonGenerar, restricciones);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.add(etiquetaCantidad, BorderLayout.WEST);

        contenido.add(panelControles, BorderLayout.NORTH);
        contenido.add(panelVistas, BorderLayout.CENTER);
        contenido.add(panelInferior, BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    private void actualizarCampoCuenta() {
        boolean requiereCuenta =
                REPORTE_POR_CUENTA.equals(selectorReportes.getSelectedItem());
        campoNumeroCuenta.setEnabled(requiereCuenta);

        if (!requiereCuenta) {
            campoNumeroCuenta.setText("");
        }
    }

    private void generarReporte() {
        String reporte = (String) selectorReportes.getSelectedItem();

        try {
            if (REPORTE_CUENTAS_ACTIVAS.equals(reporte)) {
                mostrarLista(reporteDAO.reporteCuentasActivas());
            } else if (REPORTE_CUENTAS_BLOQUEADAS.equals(reporte)) {
                mostrarLista(reporteDAO.reporteCuentasBloqueadas());
            } else if (REPORTE_CLIENTES.equals(reporte)) {
                mostrarLista(reporteDAO.reporteClientesRegistrados());
            } else if (REPORTE_HISTORIAL.equals(reporte)) {
                mostrarLista(reporteDAO.reporteHistorialTransacciones());
            } else if (REPORTE_POR_CUENTA.equals(reporte)) {
                generarReportePorCuenta();
            } else if (REPORTE_DEPOSITADO.equals(reporte)) {
                mostrarTotal(reporteDAO.totalDepositado());
            } else if (REPORTE_RETIRADO.equals(reporte)) {
                mostrarTotal(reporteDAO.totalRetirado());
            } else if (REPORTE_TRANSFERIDO.equals(reporte)) {
                mostrarTotal(reporteDAO.totalTransferido());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo generar el reporte.\n" + ex.getMessage(),
                    "Error de SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReportePorCuenta() throws SQLException {
        String numeroCuenta = campoNumeroCuenta.getText().trim();

        if (numeroCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Escribe el número de cuenta.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE);
            campoNumeroCuenta.requestFocusInWindow();
            return;
        }

        DefaultTableModel modelo =
                reporteDAO.reporteTransaccionesPorCuenta(numeroCuenta);
        mostrarLista(modelo);

        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay transacciones para esa cuenta.",
                    "Sin movimientos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarLista(DefaultTableModel modelo) {
        tabla.setModel(modelo);
        etiquetaCantidad.setText(
                "Registros encontrados: " + modelo.getRowCount());
        etiquetaCantidad.setVisible(true);
        administradorVistas.show(panelVistas, VISTA_LISTA);
    }

    private void mostrarTotal(BigDecimal total) {
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.US);
        etiquetaTotal.setText(formatoMoneda.format(total));
        etiquetaCantidad.setVisible(false);
        administradorVistas.show(panelVistas, VISTA_TOTAL);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormularioReportes formulario = new FormularioReportes();
            formulario.setVisible(true);
        });
    }
}
