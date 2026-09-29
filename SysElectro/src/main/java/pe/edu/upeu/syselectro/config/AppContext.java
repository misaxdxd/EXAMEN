package pe.edu.upeu.syselectro.config;

import pe.edu.upeu.syselectro.controller.*;
import pe.edu.upeu.syselectro.repository.*;
import pe.edu.upeu.syselectro.service.*;
import pe.edu.upeu.syselectro.service.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contenedor de dependencias manual (equivale a lo que haría Spring con @Autowired).
 * Es un Singleton: una sola instancia en toda la aplicación.
 *
 * TRABAJO EN EQUIPO: cada integrante agrega SUS líneas dentro de su bloque
 * (marcado con comentarios) para evitar conflictos al hacer merge en Git.
 */
public class AppContext {

    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // El "directorio": Clase -> Objeto
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        sembrarDatos();
        registrarServicios();
        registrarControladores();
    }

    // CAPA 1 - REPOSITORIOS (cada uno es un ArrayList en memoria)
    private void registrarRepositorios() {
        // Integrante 1
        registrar(CategoriaRepository.class, new CategoriaRepository());
        // Integrante 2
        registrar(MarcaRepository.class, new MarcaRepository());
        // Integrante 3
        registrar(EquipoRepository.class, new EquipoRepository());
        // Integrante 4
        registrar(ClienteRepository.class, new ClienteRepository());
        // Integrante 5
        registrar(ProveedorRepository.class, new ProveedorRepository());
        // Integrante 6
        registrar(UsuarioRepository.class, new UsuarioRepository());
    }

    // DATOS DE EJEMPLO (se cargan una sola vez al arrancar)
    private void sembrarDatos() {
        CategoriaRepository categorias = getBean(CategoriaRepository.class);
        MarcaRepository marcas = getBean(MarcaRepository.class);
        categorias.seedData();
        marcas.seedData();
        // Equipo necesita categorías y marcas ya cargadas
        getBean(EquipoRepository.class).seedData(categorias.findAll(), marcas.findAll());
        getBean(ClienteRepository.class).seedData();
        getBean(ProveedorRepository.class).seedData();
        getBean(UsuarioRepository.class).seedData();
    }

    // CAPA 2 - SERVICIOS (reciben su repositorio por constructor)
    private void registrarServicios() {
        // Integrante 1
        registrar(ICategoriaService.class, new CategoriaServiceImp(getBean(CategoriaRepository.class)));
        // Integrante 2
        registrar(IMarcaService.class, new MarcaServiceImp(getBean(MarcaRepository.class)));
        // Integrante 3
        registrar(IEquipoService.class, new EquipoServiceImp(getBean(EquipoRepository.class)));
        // Integrante 4
        registrar(IClienteService.class, new ClienteServiceImp(getBean(ClienteRepository.class)));
        // Integrante 5
        registrar(IProveedorService.class, new ProveedorServiceImp(getBean(ProveedorRepository.class)));
        // Integrante 6
        registrar(IUsuarioService.class, new UsuarioServiceImp(getBean(UsuarioRepository.class)));
    }

    // CAPA 3 - CONTROLADORES JavaFX (el FXMLLoader los pide con setControllerFactory)
    private void registrarControladores() {
        registrar(MainGuiController.class, new MainGuiController());
        // Integrante 1
        registrar(CategoriaController.class, new CategoriaController(getBean(ICategoriaService.class)));
        // Integrante 2
        registrar(MarcaController.class, new MarcaController(getBean(IMarcaService.class)));
        // Integrante 3
        registrar(EquipoController.class,
                new EquipoController(getBean(IEquipoService.class),
                        getBean(ICategoriaService.class),
                        getBean(IMarcaService.class)));
        // Integrante 4
        registrar(ClienteController.class, new ClienteController(getBean(IClienteService.class)));
        // Integrante 5
        registrar(ProveedorController.class, new ProveedorController(getBean(IProveedorService.class)));
        // Integrante 6
        registrar(UsuarioController.class, new UsuarioController(getBean(IUsuarioService.class)));
    }

    /** Guarda un objeto en el directorio, indexado por su tipo o interfaz. */
    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    /** Busca y devuelve un objeto por su tipo o interfaz. */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName()
                                    + "\n-> ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}
