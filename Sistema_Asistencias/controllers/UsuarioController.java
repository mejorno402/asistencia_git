package controllers;

import models.Usuario;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioController {
    private DatabaseConnection db;
    private List<Usuario> usuariosMemoria; // Para modo simulación
    private int nextId = 1;
    
    public UsuarioController() {
        this.db = DatabaseConnection.getInstance();
        this.usuariosMemoria = new ArrayList<>();
        
        // Si no hay conexión BD, inicializar datos de prueba
        if (!db.isConnected()) {
            inicializarDatosPrueba();
        }
    }
    
    // GU-01: Crear usuarios
    public boolean crearUsuario(Usuario usuario) {
        try {
            if (db.isConnected()) {
                String sql = "INSERT INTO usuarios (nombre, email, password, tipo) VALUES (?, ?, ?, ?)";
                int id = db.executeUpdate(sql, 
                    usuario.getNombre(), 
                    usuario.getEmail(), 
                    usuario.getPassword(), 
                    usuario.getTipo().toString()
                );
                
                if (id > 0) {
                    usuario.setId(id);
                    System.out.println("Usuario creado exitosamente con ID: " + id);
                    return true;
                }
            } else {
                // Modo simulación
                usuario.setId(nextId++);
                usuariosMemoria.add(usuario);
                System.out.println("Usuario creado en modo simulación: " + usuario.getNombre());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
        }
        return false;
    }
    
    // GU-02: Modificar usuarios
    public boolean modificarUsuario(Usuario usuario) {
        try {
            if (db.isConnected()) {
                String sql = "UPDATE usuarios SET nombre=?, email=?, password=?, tipo=? WHERE id=?";
                int result = db.executeUpdate(sql,
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getPassword(),
                    usuario.getTipo().toString(),
                    usuario.getId()
                );
                
                if (result > 0) {
                    System.out.println("Usuario modificado exitosamente: " + usuario.getNombre());
                    return true;
                }
            } else {
                // Modo simulación
                for (int i = 0; i < usuariosMemoria.size(); i++) {
                    if (usuariosMemoria.get(i).getId() == usuario.getId()) {
                        usuariosMemoria.set(i, usuario);
                        System.out.println("Usuario modificado en modo simulación: " + usuario.getNombre());
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
        }
        return false;
    }
    
    // GU-03: Eliminar usuarios
    public boolean eliminarUsuario(int usuarioId) {
        try {
            if (db.isConnected()) {
                String sql = "UPDATE usuarios SET activo=FALSE WHERE id=?";
                int result = db.executeUpdate(sql, usuarioId);
                
                if (result > 0) {
                    System.out.println("Usuario eliminado exitosamente (ID: " + usuarioId + ")");
                    return true;
                }
            } else {
                // Modo simulación
                usuariosMemoria.removeIf(u -> u.getId() == usuarioId);
                System.out.println("Usuario eliminado en modo simulación (ID: " + usuarioId + ")");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try {
            if (db.isConnected()) {
                String sql = "SELECT * FROM usuarios WHERE activo=TRUE ORDER BY nombre";
                ResultSet rs = db.executeQuery(sql);
                
                while (rs != null && rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setTipo(Usuario.TipoUsuario.valueOf(rs.getString("tipo")));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuarios.add(usuario);
                }
            } else {
                // Modo simulación
                usuarios = new ArrayList<>(usuariosMemoria);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    // Autenticar usuario (para login)
    public Usuario autenticarUsuario(String email, String password) {
        try {
            if (db.isConnected()) {
                String sql = "SELECT * FROM usuarios WHERE email=? AND password=? AND activo=TRUE";
                ResultSet rs = db.executeQuery(sql, email, password);
                
                if (rs != null && rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipo(Usuario.TipoUsuario.valueOf(rs.getString("tipo")));
                    return usuario;
                }
            } else {
                // Modo simulación
                for (Usuario u : usuariosMemoria) {
                    if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                        return u;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }
    
    // Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(int id) {
        try {
            if (db.isConnected()) {
                String sql = "SELECT * FROM usuarios WHERE id=? AND activo=TRUE";
                ResultSet rs = db.executeQuery(sql, id);
                
                if (rs != null && rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setTipo(Usuario.TipoUsuario.valueOf(rs.getString("tipo")));
                    return usuario;
                }
            } else {
                // Modo simulación
                for (Usuario u : usuariosMemoria) {
                    if (u.getId() == id) {
                        return u;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }
    
    private void inicializarDatosPrueba() {
        // Crear usuarios de prueba para modo simulación
        Usuario admin = new Usuario("Administrador", "admin@empresa.com", "admin123", Usuario.TipoUsuario.ADMINISTRADOR);
        admin.setId(nextId++);
        usuariosMemoria.add(admin);
        
        Usuario emp1 = new Usuario("Juan Pérez", "juan@empresa.com", "123456", Usuario.TipoUsuario.EMPLEADO);
        emp1.setId(nextId++);
        usuariosMemoria.add(emp1);
        
        Usuario emp2 = new Usuario("María García", "maria@empresa.com", "123456", Usuario.TipoUsuario.EMPLEADO);
        emp2.setId(nextId++);
        usuariosMemoria.add(emp2);
        
        System.out.println("Datos de prueba inicializados (modo simulación)");
    }
}