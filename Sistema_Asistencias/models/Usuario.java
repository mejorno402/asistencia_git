package models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private TipoUsuario tipo;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    
    public enum TipoUsuario {
        EMPLEADO, ADMINISTRADOR
    }
    
    // Constructor
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Usuario(String nombre, String email, String password, TipoUsuario tipo) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipo = tipo;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    // Métodos de utilidad
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(email, usuario.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", tipo=" + tipo +
                ", activo=" + activo +
                '}';
    }
}