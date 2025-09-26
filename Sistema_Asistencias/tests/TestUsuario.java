package tests;

import models.Usuario;
import controllers.UsuarioController;
import java.util.List;

public class TestUsuario {
    private UsuarioController controller;
    
    public TestUsuario() {
        this.controller = new UsuarioController();
    }
    
    public void ejecutarTodasLasPruebas() {
        System.out.println("=== EJECUTANDO PRUEBAS UNITARIAS - USUARIOS ===\n");
        
        testCrearUsuario();
        testModificarUsuario();
        testEliminarUsuario();
        testAutenticacion();
        testObtenerUsuarios();
        
        System.out.println("=== FIN PRUEBAS UNITARIAS - USUARIOS ===\n");
    }
    
    public void testCrearUsuario() {
        System.out.println("TEST 1: Crear Usuario");
        System.out.println("Descripción: Verifica que se puede crear un usuario correctamente");
        System.out.println("Condición: Usuario con datos válidos");
        
        // Preparar datos de prueba
        Usuario usuario = new Usuario("Test User", "test@empresa.com", "123456", Usuario.TipoUsuario.EMPLEADO);
        
        // Ejecutar
        boolean resultado = controller.crearUsuario(usuario);
        
        // Verificar
        if (resultado && usuario.getId() > 0) {
            System.out.println("✅ ÉXITO: Usuario creado correctamente");
            System.out.println("   - ID asignado: " + usuario.getId());
            System.out.println("   - Nombre: " + usuario.getNombre());
        } else {
            System.out.println("❌ FALLO: No se pudo crear el usuario");
        }
        System.out.println("--------------------\n");
    }
    
    public void testModificarUsuario() {
        System.out.println("TEST 2: Modificar Usuario");
        System.out.println("Descripción: Verifica que se puede modificar un usuario existente");
        System.out.println("Condición: Usuario existente con nuevos datos");
        
        // Crear usuario para modificar
        Usuario usuario = new Usuario("Usuario Original", "original@empresa.com", "123", Usuario.TipoUsuario.EMPLEADO);
        controller.crearUsuario(usuario);
        
        // Modificar datos
        usuario.setNombre("Usuario Modificado");
        usuario.setEmail("modificado@empresa.com");
        
        // Ejecutar
        boolean resultado = controller.modificarUsuario(usuario);
        
        // Verificar
        if (resultado) {
            Usuario usuarioModificado = controller.obtenerUsuarioPorId(usuario.getId());
            if (usuarioModificado != null && usuarioModificado.getNombre().equals("Usuario Modificado")) {
                System.out.println("✅ ÉXITO: Usuario modificado correctamente");
                System.out.println("   - Nuevo nombre: " + usuarioModificado.getNombre());
                System.out.println("   - Nuevo email: " + usuarioModificado.getEmail());
            } else {
                System.out.println("❌ FALLO: Datos no se actualizaron correctamente");
            }
        } else {
            System.out.println("❌ FALLO: No se pudo modificar el usuario");
        }
        System.out.println("--------------------\n");
    }
    
    public void testEliminarUsuario() {
        System.out.println("TEST 3: Eliminar Usuario");
        System.out.println("Descripción: Verifica que se puede eliminar un usuario");
        System.out.println("Condición: Usuario existente");
        
        // Crear usuario para eliminar
        Usuario usuario = new Usuario("Usuario a Eliminar", "eliminar@empresa.com", "123", Usuario.TipoUsuario.EMPLEADO);
        controller.crearUsuario(usuario);
        int idUsuario = usuario.getId();
        
        // Ejecutar
        boolean resultado = controller.eliminarUsuario(idUsuario);
        
        // Verificar
        if (resultado) {
            Usuario usuarioEliminado = controller.obtenerUsuarioPorId(idUsuario);
            if (usuarioEliminado == null) {
                System.out.println("✅ ÉXITO: Usuario eliminado correctamente");
                System.out.println("   - ID eliminado: " + idUsuario);
            } else {
                System.out.println("❌ FALLO: Usuario aún existe después de eliminación");
            }
        } else {
            System.out.println("❌ FALLO: No se pudo eliminar el usuario");
        }
        System.out.println("--------------------\n");
    }
    
    public void testAutenticacion() {
        System.out.println("TEST 4: Autenticación de Usuario");
        System.out.println("Descripción: Verifica el proceso de login");
        System.out.println("Condición: Usuario con credenciales válidas e inválidas");
        
        // Crear usuario para autenticar
        Usuario usuario = new Usuario("Usuario Auth", "auth@empresa.com", "password123", Usuario.TipoUsuario.EMPLEADO);
        controller.crearUsuario(usuario);
        
        // Prueba 1: Credenciales correctas
        Usuario usuarioAutenticado = controller.autenticarUsuario("auth@empresa.com", "password123");
        boolean pruebaExitosa = usuarioAutenticado != null;
        
        // Prueba 2: Credenciales incorrectas
        Usuario usuarioNoAutenticado = controller.autenticarUsuario("auth@empresa.com", "password_incorrecta");
        boolean pruebaNegativaExitosa = usuarioNoAutenticado == null;
        
        // Verificar
        if (pruebaExitosa && pruebaNegativaExitosa) {
            System.out.println("✅ ÉXITO: Autenticación funciona correctamente");
            System.out.println("   - Login válido: ✓");
            System.out.println("   - Login inválido rechazado: ✓");
        } else {
            System.out.println("❌ FALLO: Problemas en autenticación");
            if (!pruebaExitosa) System.out.println("   - No autentica credenciales válidas");
            if (!pruebaNegativaExitosa) System.out.println("   - Permite credenciales inválidas");
        }
        System.out.println("--------------------\n");
    }
    
    public void testObtenerUsuarios() {
        System.out.println("TEST 5: Obtener Lista de Usuarios");
        System.out.println("Descripción: Verifica que se pueden obtener todos los usuarios");
        System.out.println("Condición: Sistema con usuarios registrados");
        
        // Obtener usuarios antes de crear nuevos
        List<Usuario> usuariosAntes = controller.obtenerUsuarios();
        int cantidadAntes = usuariosAntes.size();
        
        // Crear usuarios de prueba
        controller.crearUsuario(new Usuario("Usuario 1", "u1@test.com", "123", Usuario.TipoUsuario.EMPLEADO));
        controller.crearUsuario(new Usuario("Usuario 2", "u2@test.com", "123", Usuario.TipoUsuario.EMPLEADO));
        
        // Obtener usuarios después
        List<Usuario> usuariosDespues = controller.obtenerUsuarios();
        int cantidadDespues = usuariosDespues.size();
        
        // Verificar
        if (cantidadDespues >= cantidadAntes + 2) {
            System.out.println("✅ ÉXITO: Lista de usuarios obtenida correctamente");
            System.out.println("   - Usuarios antes: " + cantidadAntes);
            System.out.println("   - Usuarios después: " + cantidadDespues);
            System.out.println("   - Incremento: " + (cantidadDespues - cantidadAntes));
        } else {
            System.out.println("❌ FALLO: No se obtuvieron todos los usuarios");
        }
        System.out.println("--------------------\n");
    }
    
    public static void main(String[] args) {
        TestUsuario test = new TestUsuario();
        test.ejecutarTodasLasPruebas();
    }
}