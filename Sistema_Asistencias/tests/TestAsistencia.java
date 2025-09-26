package tests;

import models.Usuario;
import models.Asistencia;
import controllers.UsuarioController;
import controllers.AsistenciaController;
import java.util.List;
import java.time.LocalTime;

public class TestAsistencia {
    private UsuarioController usuarioController;
    private AsistenciaController asistenciaController;
    private Usuario usuarioPrueba;
    
    public TestAsistencia() {
        this.usuarioController = new UsuarioController();
        this.asistenciaController = new AsistenciaController();
        
        // Crear usuario de prueba
        this.usuarioPrueba = new Usuario("Empleado Test", "test@asistencia.com", "123", Usuario.TipoUsuario.EMPLEADO);
        usuarioController.crearUsuario(usuarioPrueba);
    }
    
    public void ejecutarTodasLasPruebas() {
        System.out.println("=== EJECUTANDO PRUEBAS UNITARIAS - ASISTENCIA ===\n");
        
        testRegistrarEntrada();
        testRegistrarSalida();
        testDeteccionAtrasos();
        testDeteccionSalidaAnticipada();
        testReportes();
        
        System.out.println("=== FIN PRUEBAS UNITARIAS - ASISTENCIA ===\n");
    }
    
    public void testRegistrarEntrada() {
        System.out.println("TEST 1: Registrar Entrada");
        System.out.println("Descripción: Verifica que se puede registrar entrada de empleado");
        System.out.println("Condición: Usuario válido sin entrada registrada hoy");
        
        // Ejecutar
        boolean resultado = asistenciaController.registrarEntrada(usuarioPrueba);
        
        // Verificar
        if (resultado) {
            List<Asistencia> asistenciasHoy = asistenciaController.obtenerAsistenciasHoy();
            boolean entradaEncontrada = false;
            
            for (Asistencia a : asistenciasHoy) {
                if (a.getUsuarioId() == usuarioPrueba.getId() && a.getFechaHoraEntrada() != null) {
                    entradaEncontrada = true;
                    break;
                }
            }
            
            if (entradaEncontrada) {
                System.out.println("✅ ÉXITO: Entrada registrada correctamente");
                System.out.println("   - Usuario: " + usuarioPrueba.getNombre());
                System.out.println("   - Entrada encontrada en registros de hoy");
            } else {
                System.out.println("❌ FALLO: Entrada no encontrada en registros");
            }
        } else {
            System.out.println("❌ FALLO: No se pudo registrar la entrada");
        }
        System.out.println("--------------------\n");
    }
    
    public void testRegistrarSalida() {
        System.out.println("TEST 2: Registrar Salida");
        System.out.println("Descripción: Verifica que se puede registrar salida después de entrada");
        System.out.println("Condición: Usuario con entrada ya registrada");
        
        // Asegurar que hay entrada registrada
        asistenciaController.registrarEntrada(usuarioPrueba);
        
        // Ejecutar
        boolean resultado = asistenciaController.registrarSalida(usuarioPrueba);
        
        // Verificar
        if (resultado) {
            List<Asistencia> asistenciasHoy = asistenciaController.obtenerAsistenciasHoy();
            boolean salidaEncontrada = false;
            
            for (Asistencia a : asistenciasHoy) {
                if (a.getUsuarioId() == usuarioPrueba.getId() && 
                    a.getFechaHoraEntrada() != null && a.getFechaHoraSalida() != null) {
                    salidaEncontrada = true;
                    break;
                }
            }
            
            if (salidaEncontrada) {
                System.out.println("✅ ÉXITO: Salida registrada correctamente");
                System.out.println("   - Usuario: " + usuarioPrueba.getNombre());
                System.out.println("   - Salida encontrada en registros");
            } else {
                System.out.println("❌ FALLO: Salida no encontrada en registros");
            }
        } else {
            System.out.println("❌ FALLO: No se pudo registrar la salida");
        }
        System.out.println("--------------------\n");
    }
    
    public void testDeteccionAtrasos() {
        System.out.println("TEST 3: Detección de Atrasos");
        System.out.println("Descripción: Verifica que el sistema detecta entradas tardías");
        System.out.println("Condición: Entrada después de 9:30 AM");
        
        // Crear asistencia con hora tardía simulada
        Asistencia asistenciaTardia = new Asistencia();
        asistenciaTardia.setFechaHoraEntrada(
            java.time.LocalDateTime.now().withHour(10).withMinute(0) // 10:00 AM
        );
        
        // Verificar detección
        boolean esTardia = asistenciaTardia.esEntradaTardia();
        
        if (esTardia) {
            System.out.println("✅ ÉXITO: Atraso detectado correctamente");
            System.out.println("   - Hora límite: " + Asistencia.HORA_ENTRADA_LIMITE);
            System.out.println("   - Hora entrada: " + asistenciaTardia.getFechaHoraEntrada().toLocalTime());
            System.out.println("   - Clasificado como: TARDÍA");
        } else {
            System.out.println("❌ FALLO: No se detectó el atraso");
        }
        System.out.println("--------------------\n");
    }
    
    public void testDeteccionSalidaAnticipada() {
        System.out.println("TEST 4: Detección de Salida Anticipada");
        System.out.println("Descripción: Verifica que el sistema detecta salidas anticipadas");
        System.out.println("Condición: Salida antes de 17:30 PM");
        
        // Crear asistencia con salida anticipada simulada
        Asistencia asistenciaAnticipada = new Asistencia();
        asistenciaAnticipada.setFechaHoraSalida(
            java.time.LocalDateTime.now().withHour(16).withMinute(0) // 4:00 PM
        );
        
        // Verificar detección
        boolean esAnticipada = asistenciaAnticipada.esSalidaAnticipada();
        
        if (esAnticipada) {
            System.out.println("✅ ÉXITO: Salida anticipada detectada correctamente");
            System.out.println("   - Hora mínima: " + Asistencia.HORA_SALIDA_MINIMA);
            System.out.println("   - Hora salida: " + asistenciaAnticipada.getFechaHoraSalida().toLocalTime());
            System.out.println("   - Clasificado como: ANTICIPADA");
        } else {
            System.out.println("❌ FALLO: No se detectó la salida anticipada");
        }
        System.out.println("--------------------\n");
    }
    
    public void testReportes() {
        System.out.println("TEST 5: Generación de Reportes");
        System.out.println("Descripción: Verifica que se pueden generar los reportes requeridos");
        System.out.println("Condición: Sistema con datos de asistencia");
        
        // Registrar algunos datos de prueba
        Usuario emp2 = new Usuario("Empleado 2", "emp2@test.com", "123", Usuario.TipoUsuario.EMPLEADO);
        usuarioController.crearUsuario(emp2);
        
        asistenciaController.registrarEntrada(usuarioPrueba);
        asistenciaController.registrarEntrada(emp2);
        
        // Probar reportes
        List<Asistencia> atrasos = asistenciaController.obtenerReporteAtrasos();
        List<Asistencia> salidas = asistenciaController.obtenerReporteSalidasAnticipadas();
        List<String> inasistencias = asistenciaController.obtenerReporteInasistencias();
        
        // Verificar que los métodos funcionan (no fallan)
        boolean reportesDisponibles = true;
        String mensajeError = "";
        
        try {
            // Los reportes deben ser listas (pueden estar vacías pero no null)
            if (atrasos == null) {
                reportesDisponibles = false;
                mensajeError += "Reporte de atrasos es null. ";
            }
            if (salidas == null) {
                reportesDisponibles = false;
                mensajeError += "Reporte de salidas es null. ";
            }
            if (inasistencias == null) {
                reportesDisponibles = false;
                mensajeError += "Reporte de inasistencias es null. ";
            }
        } catch (Exception e) {
            reportesDisponibles = false;
            mensajeError = "Error al generar reportes: " + e.getMessage();
        }
        
        if (reportesDisponibles) {
            System.out.println("✅ ÉXITO: Todos los reportes se generan correctamente");
            System.out.println("   - Reporte atrasos: " + atrasos.size() + " registros");
            System.out.println("   - Reporte salidas: " + salidas.size() + " registros");
            System.out.println("   - Reporte inasistencias: " + inasistencias.size() + " registros");
        } else {
            System.out.println("❌ FALLO: Problemas en generación de reportes");
            System.out.println("   - Error: " + mensajeError);
        }
        System.out.println("--------------------\n");
    }
    
    public static void main(String[] args) {
        TestAsistencia test = new TestAsistencia();
        test.ejecutarTodasLasPruebas();
    }
}