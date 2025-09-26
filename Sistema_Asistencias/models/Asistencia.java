package models;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Asistencia {
    private int id;
    private int usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private TipoRegistro tipoRegistro;
    
    // Horarios estándar de la empresa
    public static final LocalTime HORA_ENTRADA_LIMITE = LocalTime.of(9, 30); // 9:30 AM
    public static final LocalTime HORA_SALIDA_MINIMA = LocalTime.of(17, 30); // 5:30 PM
    
    public enum TipoRegistro {
        ENTRADA, SALIDA
    }
    
    // Constructor
    public Asistencia() {}
    
    public Asistencia(int usuarioId, String usuarioNombre, TipoRegistro tipo) {
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.tipoRegistro = tipo;
        
        if (tipo == TipoRegistro.ENTRADA) {
            this.fechaHoraEntrada = LocalDateTime.now();
        } else {
            this.fechaHoraSalida = LocalDateTime.now();
        }
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    
    public LocalDateTime getFechaHoraEntrada() { return fechaHoraEntrada; }
    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) { this.fechaHoraEntrada = fechaHoraEntrada; }
    
    public LocalDateTime getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) { this.fechaHoraSalida = fechaHoraSalida; }
    
    public TipoRegistro getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(TipoRegistro tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    
    // Métodos de análisis
    public boolean esEntradaTardia() {
        if (fechaHoraEntrada == null) return false;
        return fechaHoraEntrada.toLocalTime().isAfter(HORA_ENTRADA_LIMITE);
    }
    
    public boolean esSalidaAnticipada() {
        if (fechaHoraSalida == null) return false;
        return fechaHoraSalida.toLocalTime().isBefore(HORA_SALIDA_MINIMA);
    }
    
    @Override
    public String toString() {
        return "Asistencia{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", entrada=" + fechaHoraEntrada +
                ", salida=" + fechaHoraSalida +
                ", tipo=" + tipoRegistro +
                '}';
    }
}