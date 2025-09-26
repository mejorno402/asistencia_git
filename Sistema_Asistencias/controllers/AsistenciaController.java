package controllers;

import models.Asistencia;
import models.Usuario;
import database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaController {
    private DatabaseConnection db;
    private List<Asistencia> asistenciasMemoria; // Para modo simulación
    private int nextId = 1;
    
    public AsistenciaController() {
        this.db = DatabaseConnection.getInstance();
        this.asistenciasMemoria = new ArrayList<>();
    }
    
    // CA-01: Registrar entrada
    public boolean registrarEntrada(Usuario usuario) {
        try {
            // Verificar si ya registró entrada hoy
            if (yaRegistroEntradaHoy(usuario.getId())) {
                System.out.println("El usuario ya registró entrada el día de hoy");
                return false;
            }
            
            Asistencia asistencia = new Asistencia(usuario.getId(), usuario.getNombre(), Asistencia.TipoRegistro.ENTRADA);
            
            if (db.isConnected()) {
                String sql = "INSERT INTO asistencia (usuario_id, usuario_nombre, fecha_hora_entrada, tipo_registro, fecha_registro) VALUES (?, ?, ?, ?, ?)";
                int id = db.executeUpdate(sql,
                    usuario.getId(),
                    usuario.getNombre(),
                    asistencia.getFechaHoraEntrada(),
                    asistencia.getTipoRegistro().toString(),
                    LocalDate.now()
                );
                
                if (id > 0) {
                    asistencia.setId(id);
                    String estado = asistencia.esEntradaTardia() ? " (TARDÍA)" : " (PUNTUAL)";
                    System.out.println("Entrada registrada exitosamente para " + usuario.getNombre() + estado);
                    return true;
                }
            } else {
                // Modo simulación
                asistencia.setId(nextId++);
                asistenciasMemoria.add(asistencia);
                String estado = asistencia.esEntradaTardia() ? " (TARDÍA)" : " (PUNTUAL)";
                System.out.println("Entrada registrada en simulación para " + usuario.getNombre() + estado);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al registrar entrada: " + e.getMessage());
        }
        return false;
    }
    
    // CA-01: Registrar salida
    public boolean registrarSalida(Usuario usuario) {
        try {
            if (db.isConnected()) {
                // Buscar registro de entrada del día para actualizarlo
                String sqlBuscar = "SELECT id FROM asistencia WHERE usuario_id=? AND DATE(fecha_hora_entrada)=? AND fecha_hora_salida IS NULL";
                ResultSet rs = db.executeQuery(sqlBuscar, usuario.getId(), LocalDate.now());
                
                if (rs != null && rs.next()) {
                    int registroId = rs.getInt("id");
                    String sqlUpdate = "UPDATE asistencia SET fecha_hora_salida=? WHERE id=?";
                    int result = db.executeUpdate(sqlUpdate, LocalDateTime.now(), registroId);
                    
                    if (result > 0) {
                        boolean esAnticipada = LocalDateTime.now().toLocalTime().isBefore(Asistencia.HORA_SALIDA_MINIMA);
                        String estado = esAnticipada ? " (ANTICIPADA)" : " (NORMAL)";
                        System.out.println("Salida registrada exitosamente para " + usuario.getNombre() + estado);
                        return true;
                    }
                } else {
                    System.out.println("No se encontró registro de entrada para hoy. Registre entrada primero.");
                    return false;
                }
            } else {
                // Modo simulación
                for (Asistencia a : asistenciasMemoria) {
                    if (a.getUsuarioId() == usuario.getId() && 
                        a.getFechaHoraEntrada() != null &&
                        a.getFechaHoraEntrada().toLocalDate().equals(LocalDate.now()) &&
                        a.getFechaHoraSalida() == null) {
                        
                        a.setFechaHoraSalida(LocalDateTime.now());
                        boolean esAnticipada = a.esSalidaAnticipada();
                        String estado = esAnticipada ? " (ANTICIPADA)" : " (NORMAL)";
                        System.out.println("Salida registrada en simulación para " + usuario.getNombre() + estado);
                        return true;
                    }
                }
                System.out.println("No se encontró registro de entrada para hoy. Registre entrada primero.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al registrar salida: " + e.getMessage());
        }
        return false;
    }
    
    // RE-01: Reporte de atrasos
    public List<Asistencia> obtenerReporteAtrasos() {
        List<Asistencia> atrasos = new ArrayList<>();
        
        try {
            if (db.isConnected()) {
                String sql = "SELECT * FROM vista_atrasos ORDER BY fecha DESC";
                ResultSet rs = db.executeQuery(sql);
                
                while (rs != null && rs.next()) {
                    Asistencia asistencia = new Asistencia();
                    asistencia.setUsuarioNombre(rs.getString("nombre"));
                    asistencia.setFechaHoraEntrada(rs.getTimestamp("fecha_hora_entrada").toLocalDateTime());
                    atrasos.add(asistencia);
                }
            } else {
                // Modo simulación
                for (Asistencia a : asistenciasMemoria) {
                    if (a.esEntradaTardia()) {
                        atrasos.add(a);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al generar reporte de atrasos: " + e.getMessage());
        }
        
        return atrasos;
    }
    
    // RE-02: Reporte de salidas anticipadas
    public List<Asistencia> obtenerReporteSalidasAnticipadas() {
        List<Asistencia> salidasAnticipadas = new ArrayList<>();
        
        try {
            if (db.isConnected()) {
                String sql = "SELECT * FROM vista_salidas_anticipadas ORDER BY fecha DESC";
                ResultSet rs = db.executeQuery(sql);
                
                while (rs != null && rs.next()) {
                    Asistencia asistencia = new Asistencia();
                    asistencia.setUsuarioNombre(rs.getString("nombre"));
                    asistencia.setFechaHoraSalida(rs.getTimestamp("fecha_hora_salida").toLocalDateTime());
                    salidasAnticipadas.add(asistencia);
                }
            } else {
                // Modo simulación
                for (Asistencia a : asistenciasMemoria) {
                    if (a.esSalidaAnticipada()) {
                        salidasAnticipadas.add(a);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al generar reporte de salidas anticipadas: " + e.getMessage());
        }
        
        return salidasAnticipadas;
    }
    
    // RE-03: Reporte de inasistencias
    public List<String> obtenerReporteInasistencias() {
        List<String> inasistencias = new ArrayList<>();
        
        try {
            if (db.isConnected()) {
                String sql = "SELECT nombre, email, fecha FROM vista_inasistencias ORDER BY fecha DESC, nombre";
                ResultSet rs = db.executeQuery(sql);
                
                while (rs != null && rs.next()) {
                    String registro = String.format("%s (%s) - Fecha: %s", 
                        rs.getString("nombre"), 
                        rs.getString("email"),
                        rs.getString("fecha"));
                    inasistencias.add(registro);
                }
            } else {
                // Modo simulación - simplificado
                inasistencias.add("Modo simulación - Reporte de inasistencias no disponible");
            }
        } catch (Exception e) {
            System.err.println("Error al generar reporte de inasistencias: " + e.getMessage());
        }
        
        return inasistencias;
    }
    
    // Obtener asistencias del día actual
    public List<Asistencia> obtenerAsistenciasHoy() {
        List<Asistencia> asistenciasHoy = new ArrayList<>();
        
        try {
            if (db.isConnected()) {
                String sql = "SELECT * FROM asistencia WHERE DATE(fecha_registro) = ? ORDER BY fecha_hora_entrada DESC";
                ResultSet rs = db.executeQuery(sql, LocalDate.now());
                
                while (rs != null && rs.next()) {
                    Asistencia asistencia = new Asistencia();
                    asistencia.setId(rs.getInt("id"));
                    asistencia.setUsuarioId(rs.getInt("usuario_id"));
                    asistencia.setUsuarioNombre(rs.getString("usuario_nombre"));
                    
                    Timestamp entrada = rs.getTimestamp("fecha_hora_entrada");
                    if (entrada != null) {
                        asistencia.setFechaHoraEntrada(entrada.toLocalDateTime());
                    }
                    
                    Timestamp salida = rs.getTimestamp("fecha_hora_salida");
                    if (salida != null) {
                        asistencia.setFechaHoraSalida(salida.toLocalDateTime());
                    }
                    
                    asistenciasHoy.add(asistencia);
                }
            } else {
                // Modo simulación
                LocalDate hoy = LocalDate.now();
                for (Asistencia a : asistenciasMemoria) {
                    if (a.getFechaHoraEntrada() != null && 
                        a.getFechaHoraEntrada().toLocalDate().equals(hoy)) {
                        asistenciasHoy.add(a);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener asistencias de hoy: " + e.getMessage());
        }
        
        return asistenciasHoy;
    }
    
    private boolean yaRegistroEntradaHoy(int usuarioId) {
        try {
            if (db.isConnected()) {
                String sql = "SELECT COUNT(*) FROM asistencia WHERE usuario_id=? AND DATE(fecha_hora_entrada)=?";
                ResultSet rs = db.executeQuery(sql, usuarioId, LocalDate.now());
                
                if (rs != null && rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } else {
                // Modo simulación
                LocalDate hoy = LocalDate.now();
                for (Asistencia a : asistenciasMemoria) {
                    if (a.getUsuarioId() == usuarioId && 
                        a.getFechaHoraEntrada() != null &&
                        a.getFechaHoraEntrada().toLocalDate().equals(hoy)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al verificar entrada del día: " + e.getMessage());
        }
        return false;
    }
}