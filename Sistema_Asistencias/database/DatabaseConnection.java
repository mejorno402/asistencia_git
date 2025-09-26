package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url = "jdbc:mysql://localhost:3306/sistema_asistencia";
    private String username = "root";
    private String password = "";
    
    // Constructor privado para patrón Singleton
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Conexión a base de datos establecida exitosamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
            System.out.println("Funcionando en modo simulación sin base de datos");
        } catch (SQLException e) {
            System.err.println("Error de conexión a base de datos: " + e.getMessage());
            System.out.println("Funcionando en modo simulación sin base de datos");
        }
    }
    
    // Método para obtener la instancia única
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    // Verificar si hay conexión activa
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Ejecutar consultas SELECT
    public ResultSet executeQuery(String sql, Object... params) {
        try {
            if (!isConnected()) {
                return null;
            }
            
            PreparedStatement statement = connection.prepareStatement(sql);
            
            // Establecer parámetros
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            
            return statement.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta: " + e.getMessage());
            return null;
        }
    }
    
    // Ejecutar consultas INSERT/UPDATE/DELETE
    public int executeUpdate(String sql, Object... params) {
        try {
            if (!isConnected()) {
                return 0;
            }
            
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Establecer parámetros
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            
            int result = statement.executeUpdate();
            
            // Si es INSERT, devolver el ID generado
            if (sql.trim().toUpperCase().startsWith("INSERT")) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            
            return result;
        } catch (SQLException e) {
            System.err.println("Error al ejecutar actualización: " + e.getMessage());
            return 0;
        }
    }
    
    // Cerrar conexión
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}