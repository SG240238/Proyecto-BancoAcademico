import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class FormularioTransferencias extends JFrame {

    private final TransferenciaDAO dao = new TransferenciaDAO();

    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JTextField txtMonto;

    private JTable            tablaHistorial;
    private DefaultTableModel modeloTabla;

    private JButton btnTransferir;
    private JButton btnVerHistorial;

    public FormularioTransferencias() {
        super("Banco Académico – Transferencias e Historial");
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 16, 12, 16));
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Transferencias entre cuentas", JLabel.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0x1A, 0x5F, 0x7A));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(0, 12));
        panelCentral.add(construirPanelFormulario(), BorderLayout.NORTH);
        panelCentral.add(construirPanelHistorial(),  BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0x1A, 0x5F, 0x7A), 1),
                "Datos de la transferencia",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                new Color(0x1A, 0x5F, 0x7A)));

        GridBagConstraints etiqueta = new GridBagConstraints();
        etiqueta.anchor  = GridBagConstraints.WEST;
        etiqueta.insets  = new Insets(8, 10, 4, 8);
        etiqueta.fill    = GridBagConstraints.NONE;
        etiqueta.weightx = 0;

        GridBagConstraints campo = new GridBagConstraints();
        campo.fill    = GridBagConstraints.HORIZONTAL;
        campo.insets  = new Insets(8, 0, 4, 10);
        campo.weightx = 1.0;

        etiqueta.gridx = 0; etiqueta.gridy = 0;
        panel.add(new JLabel("Cuenta origen:"), etiqueta);
        txtOrigen = crearCampoTexto("Ej: CTA-001");
        campo.gridx = 1; campo.gridy = 0;
        panel.add(txtOrigen, campo);

        etiqueta.gridy = 1;
        panel.add(new JLabel("Cuenta destino:"), etiqueta);
        txtDestino = crearCampoTexto("Ej: CTA-002");
        campo.gridy = 1;
        panel.add(txtDestino, campo);

        etiqueta.gridy = 2;
        panel.add(new JLabel("Monto:"), etiqueta);
        txtMonto = crearCampoTexto("Ej: 500.00");
        campo.gridy = 2;
        panel.add(txtMonto, campo);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        btnTransferir   = crearBoton("💸  Transferir",    new Color(0x1A, 0x5F, 0x7A));
        btnVerHistorial = crearBoton("📋  Ver historial", new Color(0x2E, 0x86, 0x48));
        panelBotones.add(btnTransferir);
        panelBotones.add(btnVerHistorial);

        GridBagConstraints gcBotones = new GridBagConstraints();
        gcBotones.gridx     = 0;
        gcBotones.gridy     = 3;
        gcBotones.gridwidth = 2;
        gcBotones.fill      = GridBagConstraints.HORIZONTAL;
        gcBotones.insets    = new Insets(10, 10, 8, 10);
        panel.add(panelBotones, gcBotones);

        btnTransferir.addActionListener(e -> accionTransferir());
        btnVerHistorial.addActionListener(e -> accionVerHistorial());

        return panel;
    }

    private JPanel construirPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0x2E, 0x86, 0x48), 1),
                "Historial de movimientos",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                new Color(0x2E, 0x86, 0x48)));

        modeloTabla = new DefaultTableModel(
                new String[]{"Fecha", "Tipo", "Monto", "Cuenta relacionada"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(24);
        tablaHistorial.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaHistorial.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaHistorial.setGridColor(new Color(0xCC, 0xCC, 0xCC));
        tablaHistorial.setShowGrid(true);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(160);

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setPreferredSize(new Dimension(0, 220));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void accionTransferir() {
        String origen   = textoReal(txtOrigen,  "Ej: CTA-001");
        String destino  = textoReal(txtDestino, "Ej: CTA-002");
        String montoStr = textoReal(txtMonto,   "Ej: 500.00");

        if (origen.isEmpty() || destino.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa un monto numérico válido.",
                    "Formato incorrecto",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            dao.transferir(origen, destino, monto);

            JOptionPane.showMessageDialog(this,
                    "✅ Transferencia realizada con éxito.\n" +
                    "Origen: "  + origen  + "\n" +
                    "Destino: " + destino + "\n" +
                    "Monto: $"  + monto,
                    "Transferencia exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            txtMonto.setText("");
            cargarHistorial(origen);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error de validación",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos:\n" + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionVerHistorial() {
        String origen = textoReal(txtOrigen, "Ej: CTA-001");

        if (origen.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe el número de cuenta origen para ver su historial.",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        cargarHistorial(origen);
    }

    private void cargarHistorial(String numCuenta) {
        try {
            DefaultTableModel nuevoModelo = dao.obtenerMovimientos(numCuenta);

            modeloTabla.setRowCount(0);
            for (int i = 0; i < nuevoModelo.getRowCount(); i++) {
                Object[] fila = new Object[nuevoModelo.getColumnCount()];
                for (int j = 0; j < fila.length; j++) {
                    fila[j] = nuevoModelo.getValueAt(i, j);
                }
                modeloTabla.addRow(fila);
            }

            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron movimientos para la cuenta: " + numCuenta,
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener el historial:\n" + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setToolTipText(placeholder);
        campo.setForeground(Color.GRAY);
        campo.setText(placeholder);
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(placeholder);
                }
            }
        });
        return campo;
    }

    private String textoReal(JTextField campo, String placeholder) {
        String val = campo.getText().trim();
        return val.equals(placeholder) ? "" : val;
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(190, 36));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            FormularioTransferencias ventana = new FormularioTransferencias();
            ventana.setVisible(true);
        });
    }
}
