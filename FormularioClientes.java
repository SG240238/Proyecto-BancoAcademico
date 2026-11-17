import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class FormularioClientes extends JFrame {

    private JTextField txtCodigo, txtDui, txtNombre, txtTelefono,
            txtCorreo, txtDireccion;
    private JComboBox<String> cmbEstado;
    private JTable tblClientes;
    private DefaultTableModel modeloTabla;
    private ClienteDAO clienteDAO;
    private int idClienteSeleccionado = -1;

    public FormularioClientes() {
        super("Gestión de Clientes");

        try {
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");
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

        JLabel title = new JLabel("Gestión de Clientes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBackground(new Color(244, 246, 249));
        content.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel formWrapper = new JPanel(new BorderLayout(10, 14));
        formWrapper.setBackground(Color.WHITE);
        formWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 232)),
                new EmptyBorder(12, 12, 12, 12)));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = new JTextField();
        txtDui = new JTextField();
        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        txtDireccion = new JTextField();

        cmbEstado = new JComboBox<>(
                new String[] { "Activo", "Inactivo", "Bloqueado" });

        agregarCampo(formulario, gbc, 0, "Código:", txtCodigo,
                "Código del cliente");
        agregarCampo(formulario, gbc, 1, "DUI:", txtDui,
                "00000000-0");
        agregarCampo(formulario, gbc, 2, "Nombre:", txtNombre,
                "Nombre completo");
        agregarCampo(formulario, gbc, 3, "Teléfono:", txtTelefono,
                "7654-3210");
        agregarCampo(formulario, gbc, 4, "Correo:", txtCorreo,
                "correo@ejemplo.com");
        agregarCampo(formulario, gbc, 5, "Dirección:", txtDireccion,
                "Dirección completa");
        agregarCombo(formulario, gbc, 6, "Estado:", cmbEstado,
                "Estado del cliente");

        JPanel botones = new JPanel();
        botones.setOpaque(false);

        JButton btnGuardar = crearBoton("Guardar",
                new Color(46, 158, 91), new Color(35, 132, 75));
        JButton btnActualizar = crearBoton("Actualizar",
                new Color(47, 128, 237), new Color(33, 103, 197));
        JButton btnEliminar = crearBoton("Eliminar",
                new Color(214, 69, 69), new Color(173, 48, 48));
        JButton btnLimpiar = crearBoton("Limpiar",
                new Color(122, 134, 154), new Color(96, 107, 124));

        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        formWrapper.add(formulario, BorderLayout.CENTER);
        formWrapper.add(botones, BorderLayout.SOUTH);

        String[] columnas = {
                "ID", "Código", "DUI", "Nombre",
                "Teléfono", "Correo", "Dirección", "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblClientes = new JTable(modeloTabla);
        tblClientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        tblClientes.setRowHeight(28);
        tblClientes.setFillsViewportHeight(true);
        tblClientes.setShowGrid(false);
        tblClientes.setSelectionBackground(new Color(174, 213, 255));
        tblClientes.setSelectionForeground(new Color(17, 24, 39));
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.getTableHeader().setBackground(
                new Color(31, 58, 95));
        tblClientes.getTableHeader().setForeground(Color.WHITE);
        tblClientes.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tblClientes.getSelectedRow() != -1) {
                cargarClienteSeleccionado(tblClientes.getSelectedRow());
            }
        });

        JTableHeader th = tblClientes.getTableHeader();
        th.setPreferredSize(new Dimension(0, 34));

        int[] anchos = { 50, 90, 120, 190, 110, 170, 220, 90 };

        for (int i = 0; i < anchos.length; i++) {
            tblClientes.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tblClientes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        content.add(formWrapper, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc,
            int fila, String texto, JTextField campo, String tooltip) {

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
                BorderFactory.createLineBorder(new Color(202, 208, 218)),
                new EmptyBorder(7, 10, 7, 10)));

        panel.add(campo, gbc);
    }

    private void agregarCombo(JPanel panel, GridBagConstraints gbc,
            int fila, String texto, JComboBox<String> campo,
            String tooltip) {

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

        campo.setToolTipText(tooltip);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(0, 36));

        panel.add(campo, gbc);
    }

    private JButton crearBoton(String texto, Color base, Color hover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(base);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    private boolean validarCampos() {
        String codigo = txtCodigo.getText().trim();
        String dui = txtDui.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (codigo.isEmpty()) {
            mostrarError("El código del cliente es obligatorio.");
            txtCodigo.requestFocus();
            return false;
        }

        if (dui.isEmpty()) {
            mostrarError("El DUI es obligatorio.");
            txtDui.requestFocus();
            return false;
        }

        if (!dui.matches("\\d{8}-\\d")) {
            mostrarError("El DUI debe tener el formato 00000000-0.");
            txtDui.requestFocus();
            return false;
        }

        if (nombre.isEmpty()) {
            mostrarError("El nombre completo es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }

        if (direccion.isEmpty()) {
            mostrarError("La dirección es obligatoria.");
            txtDireccion.requestFocus();
            return false;
        }

        if (!telefono.isEmpty()) {
            String tel = telefono.replace("-", "");

            if (!tel.matches("\\d{8}")) {
                mostrarError(
                        "El teléfono debe tener 8 dígitos.\n"
                                + "Ejemplo: 7654-3210");
                txtTelefono.requestFocus();
                return false;
            }

            txtTelefono.setText(tel);
        }

        if (!correo.isEmpty()) {
            String patron = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

            if (!Pattern.matches(patron, correo)) {
                mostrarError(
                        "El correo no tiene un formato válido.\n"
                                + "Ejemplo: usuario@correo.com");
                txtCorreo.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Validación",
                JOptionPane.WARNING_MESSAGE);
    }

    private void guardarCliente() {
        if (!validarCampos()) {
            return;
        }

        Cliente cliente = new Cliente();

        cliente.setCodigoCliente(txtCodigo.getText().trim());
        cliente.setDui(txtDui.getText().trim());
        cliente.setNombreCompleto(txtNombre.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());

        String correo = txtCorreo.getText().trim();
        cliente.setCorreoElectronico(
                correo.isEmpty() ? null : correo);

        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setEstado(
                (String) cmbEstado.getSelectedItem());

        if (clienteDAO.insertar(cliente)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cliente guardado correctamente.");

            cargarClientes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    clienteDAO.getUltimoError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente para actualizar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Cliente cliente = new Cliente();

        cliente.setIdCliente(idClienteSeleccionado);
        cliente.setCodigoCliente(txtCodigo.getText().trim());
        cliente.setDui(txtDui.getText().trim());
        cliente.setNombreCompleto(txtNombre.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());

        String correo = txtCorreo.getText().trim();
        cliente.setCorreoElectronico(
                correo.isEmpty() ? null : correo);

        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setEstado(
                (String) cmbEstado.getSelectedItem());

        if (clienteDAO.actualizar(cliente)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cliente actualizado correctamente.");

            cargarClientes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    clienteDAO.getUltimoError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente para eliminar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el cliente seleccionado?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(idClienteSeleccionado)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cliente eliminado correctamente.");

                cargarClientes();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar el cliente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
            modeloTabla.addRow(new Object[] {
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

    private void cargarClienteSeleccionado(int fila) {
        idClienteSeleccionado = Integer.parseInt(
                valorSeguro(fila, 0));

        txtCodigo.setText(valorSeguro(fila, 1));
        txtDui.setText(valorSeguro(fila, 2));
        txtNombre.setText(valorSeguro(fila, 3));
        txtTelefono.setText(valorSeguro(fila, 4));
        txtCorreo.setText(valorSeguro(fila, 5));
        txtDireccion.setText(valorSeguro(fila, 6));
        cmbEstado.setSelectedItem(valorSeguro(fila, 7));
    }

    private String valorSeguro(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormularioClientes form = new FormularioClientes();
            form.setVisible(true);
        });
    }
}