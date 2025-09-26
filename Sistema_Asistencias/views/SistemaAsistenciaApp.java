package views;

import models.Usuario;
import models.Asistencia;
import controllers.UsuarioController;
import controllers.AsistenciaController;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class SistemaAsistenciaApp {
    private static UsuarioController usuarioController = new UsuarioController();
    private static AsistenciaController asistenciaController = new AsistenciaController();
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioActual = null;
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("    SISTEMA DE ASISTENCIA EMPRESARIAL");
        System.out.println("        Empresa de Productos Químicos");
        System.out.println("==============================================");
        
        // Bucle principal de la aplicación
        while (true) {
            if (usuarioActual == null) {
                mostrarMenuLogin();
            } else if (usuarioActual.getTipo() == Usuario.TipoUsuario.ADMINISTRADOR) {
                mostrarMenuAdministrador();
            } else {
                mostrarMenuEmpleado();
            }
        }
    }
    
    private static void mostrarMenuLogin() {
        System.out.println("\n===== LOGIN =====");
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Salir del Sistema");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                iniciarSesion();
                break;
            case 2:
                System.out.println("¡Gracias por usar el Sistema de Asistencia!");
                System.exit(0);
                break;
            default:
                System.out.println("Opción inválida. Intente nuevamente.");
        }
    }
    
    private static void iniciarSesion() {
        System.out.print("\nIngrese su email: ");
        String email = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String password = scanner.nextLine();
        
        usuarioActual = usuarioController.autenticarUsuario(email, password);
        
        if (usuarioActual != null) {
            System.out.println("\n¡Bienvenido/a " + usuarioActual.getNombre() + "!");
            System.out.println("Tipo de usuario: " + usuarioActual.getTipo());
        } else {
            System.out.println("\nCredenciales incorrectas. Intente nuevamente.");
        }
    }
    
    private static void mostrarMenuAdministrador() {
        System.out.println("\n===== MENÚ ADMINISTRADOR =====");
        System.out.println("Usuario: " + usuarioActual.getNombre());
        System.out.println("1. Gestión de Usuarios");
        System.out.println("2. Reportes");
        System.out.println("3. Ver Asistencias del Día");
        System.out.println("4. Registrar Mi Asistencia");
        System.out.println("5. Cerrar Sesión");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                menuGestionUsuarios();
                break;
            case 2:
                menuReportes();
                break;
            case 3:
                verAsistenciasDelDia();
                break;
            case 4:
                menuAsistenciaPersonal();
                break;
            case 5:
                cerrarSesion();
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }
    
    private static void mostrarMenuEmpleado() {
        System.out.println("\n===== MENÚ EMPLEADO =====");
        System.out.println("Usuario: " + usuarioActual.getNombre());
        System.out.println("1. Registrar Entrada");
        System.out.println("2. Registrar Salida");
        System.out.println("3. Ver Mi Asistencia Hoy");
        System.out.println("4. Cerrar Sesión");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                registrarEntrada();
                break;
            case 2:
                registrarSalida();
                break;
            case 3:
                verMiAsistenciaHoy();
                break;
            case 4:
                cerrarSesion();
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }
    
    private static void menuGestionUsuarios() {
        System.out.println("\n===== GESTIÓN DE USUARIOS =====");
        System.out.println("1. Crear Usuario");
        System.out.println("2. Modificar Usuario");
        System.out.println("3. Eliminar Usuario");
        System.out.println("4. Ver Todos los Usuarios");
        System.out.println("5. Volver");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                crearUsuario();
                break;
            case 2:
                modificarUsuario();
                break;
            case 3:
                eliminarUsuario();
                break;
            case 4:
                verTodosLosUsuarios();
                break;
            case 5:
                return;
            default:
                System.out.println("Opción inválida.");
        }
    }
    
    private static void crearUsuario() {
        System.out.println("\n--- CREAR USUARIO ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        System.out.print("Tipo (1=Empleado, 2=Administrador): ");
        int tipo = leerEntero();
        
        Usuario.TipoUsuario tipoUsuario = (tipo == 2) ? 
            Usuario.TipoUsuario.ADMINISTRADOR : Usuario.TipoUsuario.EMPLEADO;
        
        Usuario nuevoUsuario = new Usuario(nombre, email, password, tipoUsuario);
        
        if (usuarioController.crearUsuario(nuevoUsuario)) {
            System.out.println("Usuario creado exitosamente!");
        } else {
            System.out.println("Error al crear usuario.");
        }
    }
    
    private static void modificarUsuario() {
        System.out.println("\n--- MODIFICAR USUARIO ---");
        verTodosLosUsuarios();
        System.out.print("Ingrese ID del usuario a modificar: ");
        int id = leerEntero();
        
        Usuario usuario = usuarioController.obtenerUsuarioPorId(id);
        if (usuario != null) {
            System.out.println("Usuario actual: " + usuario.getNombre());
            System.out.print("Nuevo nombre (Enter para mantener): ");
            String nombre = scanner.nextLine();
            if (!nombre.isEmpty()) {
                usuario.setNombre(nombre);
            }
            
            System.out.print("Nuevo email (Enter para mantener): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) {
                usuario.setEmail(email);
            }
            
            System.out.print("Nueva contraseña (Enter para mantener): ");
            String password = scanner.nextLine();
            if (!password.isEmpty()) {
                usuario.setPassword(password);
            }
            
            if (usuarioController.modificarUsuario(usuario)) {
                System.out.println("Usuario modificado exitosamente!");
            } else {
                System.out.println("Error al modificar usuario.");
            }
        } else {
            System.out.println("Usuario no encontrado.");
        }
    }
    
    private static void eliminarUsuario() {
        System.out.println("\n--- ELIMINAR USUARIO ---");
        verTodosLosUsuarios();
        System.out.print("Ingrese ID del usuario a eliminar: ");
        int id = leerEntero();
        
        if (id == usuarioActual.getId()) {
            System.out.println("No puede eliminar su propio usuario.");
            return;
        }
        
        System.out.print("¿Está seguro? (s/n): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("s")) {
            if (usuarioController.eliminarUsuario(id)) {
                System.out.println("Usuario eliminado exitosamente!");
            } else {
                System.out.println("Error al eliminar usuario.");
            }
        }
    }
    
    private static void verTodosLosUsuarios() {
        System.out.println("\n--- LISTA DE USUARIOS ---");
        List<Usuario> usuarios = usuarioController.obtenerUsuarios();
        
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            System.out.printf("%-5s %-20s %-25s %-15s%n", "ID", "Nombre", "Email", "Tipo");
            System.out.println("---------------------------------------------------------------");
            for (Usuario u : usuarios) {
                System.out.printf("%-5d %-20s %-25s %-15s%n", 
                    u.getId(), u.getNombre(), u.getEmail(), u.getTipo());
            }
        }
    }
    
    private static void menuReportes() {
        System.out.println("\n===== REPORTES =====");
        System.out.println("1. Reporte de Atrasos");
        System.out.println("2. Reporte de Salidas Anticipadas");
        System.out.println("3. Reporte de Inasistencias");
        System.out.println("4. Volver");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                mostrarReporteAtrasos();
                break;
            case 2:
                mostrarReporteSalidasAnticipadas();
                break;
            case 3:
                mostrarReporteInasistencias();
                break;
            case 4:
                return;
            default:
                System.out.println("Opción inválida.");
        }
    }
    
    private static void mostrarReporteAtrasos() {
        System.out.println("\n--- REPORTE DE ATRASOS ---");
        List<Asistencia> atrasos = asistenciaController.obtenerReporteAtrasos();
        
        if (atrasos.isEmpty()) {
            System.out.println("No hay registros de atrasos.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.printf("%-20s %-20s%n", "Empleado", "Fecha y Hora");
            System.out.println("----------------------------------------");
            for (Asistencia a : atrasos) {
                System.out.printf("%-20s %-20s%n", 
                    a.getUsuarioNombre(), 
                    a.getFechaHoraEntrada().format(formatter));
            }
        }
    }
    
    private static void mostrarReporteSalidasAnticipadas() {
        System.out.println("\n--- REPORTE DE SALIDAS ANTICIPADAS ---");
        List<Asistencia> salidas = asistenciaController.obtenerReporteSalidasAnticipadas();
        
        if (salidas.isEmpty()) {
            System.out.println("No hay registros de salidas anticipadas.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.printf("%-20s %-20s%n", "Empleado", "Fecha y Hora");
            System.out.println("----------------------------------------");
            for (Asistencia a : salidas) {
                System.out.printf("%-20s %-20s%n", 
                    a.getUsuarioNombre(), 
                    a.getFechaHoraSalida().format(formatter));
            }
        }
    }
    
    private static void mostrarReporteInasistencias() {
        System.out.println("\n--- REPORTE DE INASISTENCIAS ---");
        List<String> inasistencias = asistenciaController.obtenerReporteInasistencias();
        
        if (inasistencias.isEmpty()) {
            System.out.println("No hay registros de inasistencias.");
        } else {
            for (String registro : inasistencias) {
                System.out.println(registro);
            }
        }
    }
    
    private static void verAsistenciasDelDia() {
        System.out.println("\n--- ASISTENCIAS DEL DÍA ---");
        List<Asistencia> asistencias = asistenciaController.obtenerAsistenciasHoy();
        
        if (asistencias.isEmpty()) {
            System.out.println("No hay registros de asistencia para hoy.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            System.out.printf("%-20s %-10s %-10s%n", "Empleado", "Entrada", "Salida");
            System.out.println("--------------------------------------------");
            for (Asistencia a : asistencias) {
                String entrada = a.getFechaHoraEntrada() != null ? 
                    a.getFechaHoraEntrada().format(formatter) : "---";
                String salida = a.getFechaHoraSalida() != null ? 
                    a.getFechaHoraSalida().format(formatter) : "---";
                    
                System.out.printf("%-20s %-10s %-10s%n", 
                    a.getUsuarioNombre(), entrada, salida);
            }
        }
    }
    
    private static void menuAsistenciaPersonal() {
        System.out.println("\n--- MI ASISTENCIA ---");
        System.out.println("1. Registrar Entrada");
        System.out.println("2. Registrar Salida");
        System.out.println("3. Volver");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                registrarEntrada();
                break;
            case 2:
                registrarSalida();
                break;
            case 3:
                return;
        }
    }
    
    private static void registrarEntrada() {
        if (asistenciaController.registrarEntrada(usuarioActual)) {
            System.out.println("¡Entrada registrada exitosamente!");
        } else {
            System.out.println("Error al registrar entrada.");
        }
    }
    
    private static void registrarSalida() {
        if (asistenciaController.registrarSalida(usuarioActual)) {
            System.out.println("¡Salida registrada exitosamente!");
        } else {
            System.out.println("Error al registrar salida.");
        }
    }
    
    private static void verMiAsistenciaHoy() {
        System.out.println("\n--- MI ASISTENCIA DE HOY ---");
        List<Asistencia> asistencias = asistenciaController.obtenerAsistenciasHoy();
        
        boolean encontrado = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (Asistencia a : asistencias) {
            if (a.getUsuarioId() == usuarioActual.getId()) {
                encontrado = true;
                System.out.println("Entrada: " + (a.getFechaHoraEntrada() != null ? 
                    a.getFechaHoraEntrada().format(formatter) : "No registrada"));
                System.out.println("Salida: " + (a.getFechaHoraSalida() != null ? 
                    a.getFechaHoraSalida().format(formatter) : "No registrada"));
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("No hay registros de asistencia para hoy.");
        }
    }
    
    private static void cerrarSesion() {
        System.out.println("Sesión cerrada. ¡Hasta luego " + usuarioActual.getNombre() + "!");
        usuarioActual = null;
    }
    
    private static int leerEntero() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Valor inválido
        }
    }
}