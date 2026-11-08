import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class FormularioClientes extends JFrame {
    private JTextField txtCodigo;
    private JTextField txtDui;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JComboBox<String> cmbEstado;
    private JTable tblClientes;
    private DefaultTableModel modeloTabla;
    private ClienteDAO clienteDAO;
    private int idClienteSeleccionado = -1;

    public FormularioClientes() {
        super("Gestión de Clientes");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        clienteDAO = new ClienteDAO();
        initComponents();
        cargarClientes();
        txtCodigo.requestFocusInWindow();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(new Color(244, 246, 249));
        setLayout(new BorderLayout(18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(31, 58, 95));
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel titleLabel = new JLabel("Gestión de Clientes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBackground(new Color(244, 246, 249));
        content.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel formWrapper = new JPanel(new BorderLayout(10, 14));
        formWrapper.setBackground(Color.WHITE);
        formWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 232), 1),
                new EmptyBorder(12, 12, 12, 12)));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = new JTextField();
        txtDui = new JTextField();
        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        txtDireccion = new JTextField();
        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo", "Bloqueado"});

        agregarCampo(panelFormulario, gbc, 0, "Código:", txtCodigo, "Código del cliente");
        agregarCampo(panelFormulario, gbc, 1, "DUI:", txtDui, "00000000-0");
        agregarCampo(panelFormulario, gbc, 2, "Nombre:", txtNombre, "Nombre completo del cliente");
        agregarCampo(panelFormulario, gbc, 3, "Teléfono:", txtTelefono, "Número de contacto");
        agregarCampo(panelFormulario, gbc, 4, "Correo:", txtCorreo, "correo@ejemplo.com");
        agregarCampo(panelFormulario, gbc, 5, "Dirección:", txtDireccion, "Dirección completa");
        agregarCombo(panelFormulario, gbc, 6, "Estado:", cmbEstado, "Estado del cliente");

        JPanel panelBotones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 10));
        panelBotones.setOpaque(false);

        JButton btnGuardar = crearBoton("Guardar", new Color(46, 158, 91), new Color(35, 132, 75));
        JButton btnActualizar = crearBoton("Actualizar", new Color(47, 128, 237), new Color(33, 103, 197));
        JButton btnEliminar = crearBoton("Eliminar", new Color(214, 69, 69), new Color(173, 48, 48));
        JButton btnLimpiar = crearBoton("Limpiar", new Color(122, 134, 154), new Color(96, 107, 124));

        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        formWrapper.add(panelFormulario, BorderLayout.CENTER);
        formWrapper.add(panelBotones, BorderLayout.SOUTH);

        String[] columnas = {"ID", "Código", "DUI", "Nombre", "Teléfono", "Correo", "Dirección", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblClientes = new JTable(modeloTabla);
        tblClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblClientes.setRowHeight(28);
        tblClientes.setFillsViewportHeight(true);
        tblClientes.setShowGrid(false);
        tblClientes.setIntercellSpacing(new java.awt.Dimension(0, 1));
        tblClientes.setSelectionBackground(new Color(174, 213, 255));
        tblClientes.setSelectionForeground(new Color(17, 24, 39));
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.getTableHeader().setBackground(new Color(31, 58, 95));
        tblClientes.getTableHeader().setForeground(Color.WHITE);
        tblClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblClientes.setRowMargin(2);
        tblClientes.setOpaque(true);
        tblClientes.setBackground(Color.WHITE);
        tblClientes.setGridColor(new Color(232, 235, 240));
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblClientes.getSelectedRow() != -1) {
                cargarClienteSeleccionado(tblClientes.getSelectedRow());
            }
        });

        JTableHeader headerTable = tblClientes.getTableHeader();
        headerTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerTable.setPreferredSize(new Dimension(0, 34));

        tblClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblClientes.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblClientes.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblClientes.getColumnModel().getColumn(3).setPreferredWidth(190);
        tblClientes.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblClientes.getColumnModel().getColumn(5).setPreferredWidth(170);
        tblClientes.getColumnModel().getColumn(6).setPreferredWidth(220);
        tblClientes.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(tblClientes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        content.add(formWrapper, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String texto, JTextField campo, String tooltip) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.15;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(38, 46, 60));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.85;
        gbc.anchor = GridBagConstraints.LINE_START;
        campo.setToolTipText(tooltip);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(0, 36));
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(202, 208, 218), 1),
                new EmptyBorder(7, 10, 7, 10)));
        panel.add(campo, gbc);
    }

    private void agregarCombo(JPanel panel, GridBagConstraints gbc, int fila, String texto, JComboBox<String> campo, String tooltip) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.15;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(38, 46, 60));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.85;
        gbc.anchor = GridBagConstraints.LINE_START;
        campo.setToolTipText(tooltip);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(0, 36));
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(202, 208, 218), 1),
                new EmptyBorder(0, 8, 0, 8)));
        panel.add(campo, gbc);
    }

    private JButton crearBoton(String texto, Color base, Color hover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(base);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setOpaque(true);
        boton.setPreferredSize(new Dimension(140, 40));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(base);
            }
        });
        return boton;
    }

    private void guardarCliente() {
        if (txtCodigo.getText().trim().isEmpty() || txtDui.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar al menos Código, DUI y Nombre.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setCodigoCliente(txtCodigo.getText().trim());
        cliente.setDui(txtDui.getText().trim());
        cliente.setNombreCompleto(txtNombre.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setCorreoElectronico(txtCorreo.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setEstado((String) cmbEstado.getSelectedItem());

        if (clienteDAO.insertar(cliente)) {
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente.");
            cargarClientes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setIdCliente(idClienteSeleccionado);
        cliente.setCodigoCliente(txtCodigo.getText().trim());
        cliente.setDui(txtDui.getText().trim());
        cliente.setNombreCompleto(txtNombre.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setCorreoElectronico(txtCorreo.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setEstado((String) cmbEstado.getSelectedItem());

        if (clienteDAO.actualizar(cliente)) {
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            cargarClientes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el cliente seleccionado?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(idClienteSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
                cargarClientes();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        idClienteSeleccionado = -1;
        txtCodigo.setText("");
        txtDui.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        cmbEstado.setSelectedIndex(0);
        tblClientes.clearSelection();
        txtCodigo.requestFocus();
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        for (Cliente cliente : clienteDAO.listar()) {
            modeloTabla.addRow(new Object[]{
                    cliente.getIdCliente(),
                    cliente.getCodigoCliente(),
                    cliente.getDui(),
                    cliente.getNombreCompleto(),
                    cliente.getTelefono(),
                    cliente.getCorreoElectronico(),
                    cliente.getDireccion(),
                    cliente.getEstado()
            });
        }
    }

    private void cargarClienteSeleccionado(int filaSeleccionada) {
        idClienteSeleccionado = Integer.parseInt(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
        txtCodigo.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());
        txtDui.setText(modeloTabla.getValueAt(filaSeleccionada, 2).toString());
        txtNombre.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString());
        txtTelefono.setText(modeloTabla.getValueAt(filaSeleccionada, 4).toString());
        txtCorreo.setText(modeloTabla.getValueAt(filaSeleccionada, 5).toString());
        txtDireccion.setText(modeloTabla.getValueAt(filaSeleccionada, 6).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 7).toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormularioClientes form = new FormularioClientes();
            form.setVisible(true);
        });
    }
}
