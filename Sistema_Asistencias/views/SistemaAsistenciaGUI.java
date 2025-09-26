
package views;

import models.Usuario;
import models.Asistencia;
import controllers.UsuarioController;
import controllers.AsistenciaController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class SistemaAsistenciaGUI extends JFrame {
    private UsuarioController usuarioController = new UsuarioController();
    private AsistenciaController asistenciaController = new AsistenciaController();
    private Usuario usuarioActual = null;

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Panels
    private JPanel loginPanel = new JPanel(new GridBagLayout());
    private JPanel adminPanel = new JPanel(new GridLayout(0, 1, 10, 10));
    private JPanel empleadoPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    public SistemaAsistenciaGUI() {
        setTitle("Sistema de Asistencias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        // Login Panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(20);
        JLabel lblPass = new JLabel("Contraseña:");
        JPasswordField txtPass = new JPasswordField(20);
        JButton btnLogin = new JButton("Iniciar Sesión");
        JButton btnSalirLogin = new JButton("Salir");

        gbc.gridx = 0; gbc.gridy = 0; loginPanel.add(lblEmail, gbc);
        gbc.gridx = 1; loginPanel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 1; loginPanel.add(lblPass, gbc);
        gbc.gridx = 1; loginPanel.add(txtPass, gbc);
        gbc.gridx = 0; gbc.gridy = 2; loginPanel.add(btnLogin, gbc);
        gbc.gridx = 1; loginPanel.add(btnSalirLogin, gbc);

        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String pass = new String(txtPass.getPassword());
            usuarioActual = usuarioController.autenticarUsuario(email, pass);
            if (usuarioActual != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido/a " + usuarioActual.getNombre());
                if (usuarioActual.getTipo() == Usuario.TipoUsuario.ADMINISTRADOR) {
                    cardLayout.show(mainPanel, "admin");
                } else {
                    cardLayout.show(mainPanel, "empleado");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.");
            }
        });
        btnSalirLogin.addActionListener(e -> System.exit(0));

        // Admin Panel
        adminPanel.add(new JLabel("Menú Administrador", SwingConstants.CENTER));
        JButton btnGestionUsuarios = new JButton("Gestión de Usuarios");
        JButton btnReportes = new JButton("Reportes");
        JButton btnVerAsistenciasDia = new JButton("Ver Asistencias del Día");
        JButton btnRegistrarAsistencia = new JButton("Registrar Mi Asistencia");
        JButton btnCerrarSesionAdmin = new JButton("Cerrar Sesión");
        adminPanel.add(btnGestionUsuarios);
        adminPanel.add(btnReportes);
        adminPanel.add(btnVerAsistenciasDia);
        adminPanel.add(btnRegistrarAsistencia);
        adminPanel.add(btnCerrarSesionAdmin);

        btnGestionUsuarios.addActionListener(e -> mostrarDialogoGestionUsuarios());
        btnReportes.addActionListener(e -> mostrarDialogoReportes());
        btnVerAsistenciasDia.addActionListener(e -> mostrarDialogoAsistenciasDelDia());
        btnRegistrarAsistencia.addActionListener(e -> mostrarDialogoAsistenciaPersonal());
        btnCerrarSesionAdmin.addActionListener(e -> {
            if (usuarioActual != null) {
                JOptionPane.showMessageDialog(this, "Sesión cerrada. ¡Hasta luego, " + usuarioActual.getNombre() + "!");
            }
            usuarioActual = null;
            cardLayout.show(mainPanel, "login");
        });

        // Empleado Panel
        empleadoPanel.add(new JLabel("Menú Empleado", SwingConstants.CENTER));
        JButton btnRegistrarEntrada = new JButton("Registrar Entrada");
        JButton btnRegistrarSalida = new JButton("Registrar Salida");
        JButton btnVerMiAsistencia = new JButton("Ver Mi Asistencia Hoy");
        JButton btnCerrarSesionEmp = new JButton("Cerrar Sesión");
        empleadoPanel.add(btnRegistrarEntrada);
        empleadoPanel.add(btnRegistrarSalida);
        empleadoPanel.add(btnVerMiAsistencia);
        empleadoPanel.add(btnCerrarSesionEmp);

        btnRegistrarEntrada.addActionListener(e -> {
            if (asistenciaController.registrarEntrada(usuarioActual)) {
                JOptionPane.showMessageDialog(this, "¡Entrada registrada exitosamente!");
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar entrada.");
            }
        });
        btnRegistrarSalida.addActionListener(e -> {
            if (asistenciaController.registrarSalida(usuarioActual)) {
                JOptionPane.showMessageDialog(this, "¡Salida registrada exitosamente!");
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar salida.");
            }
        });
        btnVerMiAsistencia.addActionListener(e -> mostrarDialogoMiAsistenciaHoy());
        btnCerrarSesionEmp.addActionListener(e -> {
            if (usuarioActual != null) {
                JOptionPane.showMessageDialog(this, "Sesión cerrada. ¡Hasta luego, " + usuarioActual.getNombre() + "!");
            }
            usuarioActual = null;
            cardLayout.show(mainPanel, "login");
        });

        // CardLayout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(adminPanel, "admin");
        mainPanel.add(empleadoPanel, "empleado");
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    // --- Diálogos y funciones auxiliares ---
    private void mostrarDialogoGestionUsuarios() {
        String[] opciones = {"Crear Usuario", "Modificar Usuario", "Eliminar Usuario", "Ver Todos", "Volver"};
        int op = JOptionPane.showOptionDialog(this, "Seleccione una opción:", "Gestión de Usuarios",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        switch (op) {
            case 0: crearUsuarioGUI(); break;
            case 1: modificarUsuarioGUI(); break;
            case 2: eliminarUsuarioGUI(); break;
            case 3: verTodosLosUsuariosGUI(); break;
        }
    }

    private void crearUsuarioGUI() {
        JTextField nombre = new JTextField();
        JTextField email = new JTextField();
        JPasswordField pass = new JPasswordField();
        String[] tipos = {"Empleado", "Administrador"};
        JComboBox<String> tipo = new JComboBox<>(tipos);
        Object[] fields = {"Nombre:", nombre, "Email:", email, "Contraseña:", pass, "Tipo:", tipo};
        int res = JOptionPane.showConfirmDialog(this, fields, "Crear Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Usuario.TipoUsuario t = tipo.getSelectedIndex() == 1 ? Usuario.TipoUsuario.ADMINISTRADOR : Usuario.TipoUsuario.EMPLEADO;
            Usuario nuevo = new Usuario(nombre.getText(), email.getText(), new String(pass.getPassword()), t);
            if (usuarioController.crearUsuario(nuevo)) {
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente!");
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario.");
            }
        }
    }

    private void modificarUsuarioGUI() {
        List<Usuario> usuarios = usuarioController.obtenerUsuarios();
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
            return;
        }
        String[] ids = usuarios.stream().map(u -> u.getId() + ": " + u.getNombre()).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione usuario:", "Modificar Usuario",
                JOptionPane.PLAIN_MESSAGE, null, ids, ids[0]);
        if (sel == null) return;
        int id = Integer.parseInt(sel.split(":")[0]);
        Usuario usuario = usuarioController.obtenerUsuarioPorId(id);
        if (usuario == null) return;
        JTextField nombre = new JTextField(usuario.getNombre());
        JTextField email = new JTextField(usuario.getEmail());
        JPasswordField pass = new JPasswordField(usuario.getPassword());
        String[] tipos = {"Empleado", "Administrador"};
        JComboBox<String> tipo = new JComboBox<>(tipos);
        tipo.setSelectedIndex(usuario.getTipo() == Usuario.TipoUsuario.ADMINISTRADOR ? 1 : 0);
        Object[] fields = {"Nombre:", nombre, "Email:", email, "Contraseña:", pass, "Tipo:", tipo};
        int res = JOptionPane.showConfirmDialog(this, fields, "Modificar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            usuario.setNombre(nombre.getText());
            usuario.setEmail(email.getText());
            usuario.setPassword(new String(pass.getPassword()));
            usuario.setTipo(tipo.getSelectedIndex() == 1 ? Usuario.TipoUsuario.ADMINISTRADOR : Usuario.TipoUsuario.EMPLEADO);
            if (usuarioController.modificarUsuario(usuario)) {
                JOptionPane.showMessageDialog(this, "Usuario modificado exitosamente!");
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar usuario.");
            }
        }
    }

    private void eliminarUsuarioGUI() {
        List<Usuario> usuarios = usuarioController.obtenerUsuarios();
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
            return;
        }
        String[] ids = usuarios.stream().map(u -> u.getId() + ": " + u.getNombre()).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione usuario:", "Eliminar Usuario",
                JOptionPane.PLAIN_MESSAGE, null, ids, ids[0]);
        if (sel == null) return;
        int id = Integer.parseInt(sel.split(":")[0]);
        if (usuarioActual != null && id == usuarioActual.getId()) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propio usuario.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (usuarioController.eliminarUsuario(id)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente!");
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario.");
            }
        }
    }

    private void verTodosLosUsuariosGUI() {
        List<Usuario> usuarios = usuarioController.obtenerUsuarios();
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-25s %-15s\n", "ID", "Nombre", "Email", "Tipo"));
        for (Usuario u : usuarios) {
            sb.append(String.format("%-5d %-20s %-25s %-15s\n", u.getId(), u.getNombre(), u.getEmail(), u.getTipo()));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Lista de Usuarios", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDialogoReportes() {
        String[] opciones = {"Atrasos", "Salidas Anticipadas", "Inasistencias", "Volver"};
        int op = JOptionPane.showOptionDialog(this, "Seleccione un reporte:", "Reportes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        switch (op) {
            case 0: mostrarReporteAtrasosGUI(); break;
            case 1: mostrarReporteSalidasAnticipadasGUI(); break;
            case 2: mostrarReporteInasistenciasGUI(); break;
        }
    }

    private void mostrarReporteAtrasosGUI() {
        List<Asistencia> atrasos = asistenciaController.obtenerReporteAtrasos();
        if (atrasos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros de atrasos.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %-20s\n", "Empleado", "Fecha y Hora"));
        for (Asistencia a : atrasos) {
            sb.append(String.format("%-20s %-20s\n", a.getUsuarioNombre(), a.getFechaHoraEntrada().format(formatter)));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reporte de Atrasos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarReporteSalidasAnticipadasGUI() {
        List<Asistencia> salidas = asistenciaController.obtenerReporteSalidasAnticipadas();
        if (salidas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros de salidas anticipadas.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %-20s\n", "Empleado", "Fecha y Hora"));
        for (Asistencia a : salidas) {
            sb.append(String.format("%-20s %-20s\n", a.getUsuarioNombre(), a.getFechaHoraSalida().format(formatter)));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reporte de Salidas Anticipadas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarReporteInasistenciasGUI() {
        List<String> inasistencias = asistenciaController.obtenerReporteInasistencias();
        if (inasistencias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros de inasistencias.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String registro : inasistencias) {
            sb.append(registro).append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reporte de Inasistencias", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDialogoAsistenciasDelDia() {
        List<Asistencia> asistencias = asistenciaController.obtenerAsistenciasHoy();
        if (asistencias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros de asistencia para hoy.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %-10s %-10s\n", "Empleado", "Entrada", "Salida"));
        for (Asistencia a : asistencias) {
            String entrada = a.getFechaHoraEntrada() != null ? a.getFechaHoraEntrada().format(formatter) : "---";
            String salida = a.getFechaHoraSalida() != null ? a.getFechaHoraSalida().format(formatter) : "---";
            sb.append(String.format("%-20s %-10s %-10s\n", a.getUsuarioNombre(), entrada, salida));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Asistencias del Día", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDialogoAsistenciaPersonal() {
        String[] opciones = {"Registrar Entrada", "Registrar Salida", "Volver"};
        int op = JOptionPane.showOptionDialog(this, "Seleccione una opción:", "Mi Asistencia",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        switch (op) {
            case 0:
                if (asistenciaController.registrarEntrada(usuarioActual)) {
                    JOptionPane.showMessageDialog(this, "¡Entrada registrada exitosamente!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar entrada.");
                }
                break;
            case 1:
                if (asistenciaController.registrarSalida(usuarioActual)) {
                    JOptionPane.showMessageDialog(this, "¡Salida registrada exitosamente!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar salida.");
                }
                break;
        }
    }

    private void mostrarDialogoMiAsistenciaHoy() {
        List<Asistencia> asistencias = asistenciaController.obtenerAsistenciasHoy();
        boolean encontrado = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        for (Asistencia a : asistencias) {
            if (a.getUsuarioId() == usuarioActual.getId()) {
                encontrado = true;
                sb.append("Entrada: ").append(a.getFechaHoraEntrada() != null ? a.getFechaHoraEntrada().format(formatter) : "No registrada").append("\n");
                sb.append("Salida: ").append(a.getFechaHoraSalida() != null ? a.getFechaHoraSalida().format(formatter) : "No registrada").append("\n");
                break;
            }
        }
        if (!encontrado) {
            sb.append("No hay registros de asistencia para hoy.");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Mi Asistencia de Hoy", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SistemaAsistenciaGUI().setVisible(true);
        });
    }
}
