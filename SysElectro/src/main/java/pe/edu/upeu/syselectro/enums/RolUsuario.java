package pe.edu.upeu.syselectro.enums;

public enum RolUsuario {
    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    ALMACENERO("Almacenero");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
