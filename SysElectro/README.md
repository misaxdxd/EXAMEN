# SysElectro

Sistema de escritorio para la **venta de equipos electrónicos**, hecho con **Java 21, JavaFX 21 y Maven**.
Los datos se guardan en **ArrayList** (memoria): al cerrar la aplicación se pierden y se vuelven a cargar
los datos de ejemplo al abrirla.

Está basado en la arquitectura de SysVentas (POO2026-2-G2):
`Model -> Repository -> Service -> Controller -> View (FXML)` con inyección de dependencias manual (`AppContext`).

## Ejecutar

Requisitos: **JDK 21** instalado.

```bash
./mvnw clean javafx:run          # Linux / Mac
mvnw.cmd clean javafx:run        # Windows
```

En IntelliJ IDEA: abrir la carpeta como proyecto Maven y ejecutar `SysElectro` (o `App`).

## Reparto de módulos (un CRUD por integrante)

| Integrante | CRUD | Archivos propios (nombre base) | Depende de |
|-----------:|------|--------------------------------|------------|
| 1 | Categoría | `Categoria*`, `main_categoria.fxml` | - |
| 2 | Marca | `Marca*`, `main_marca.fxml` | - |
| 3 | Equipo electrónico | `Equipo*`, `EstadoEquipo`, `main_equipo.fxml` | Categoría y Marca |
| 4 | Cliente | `Cliente*`, `TipoDocumento`, `main_cliente.fxml` | - |
| 5 | Proveedor | `Proveedor*`, `main_proveedor.fxml` | - |
| 6 | Usuario (vendedor) | `Usuario*`, `RolUsuario`, `main_usuario.fxml` | - |

`*` = Model, Repository, IService, ServiceImp y Controller de ese módulo.

Archivos compartidos (coordinar al hacer merge; cada integrante solo toca SUS líneas):
`config/AppContext.java`, `controller/MainGuiController.java`, `view/maingui.fxml`.

## Flujo de trabajo con Git

1. Una persona sube el proyecto a GitHub (rama `main`) y agrega a los demás como colaboradores.
2. Cada integrante clona y crea su rama: `git checkout -b feature/<modulo>`.
3. Trabaja solo en sus archivos y hace `git commit` / `git push origin feature/<modulo>`.
4. Se integran las ramas de a una (Pull Request). Si hay conflicto en `AppContext`, conservar ambas líneas.

Más detalle y todo el código explicado en el manual PDF.
