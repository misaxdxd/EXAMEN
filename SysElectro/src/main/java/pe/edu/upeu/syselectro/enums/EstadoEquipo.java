package pe.edu.upeu.syselectro.enums;

public enum EstadoEquipo {
    NUEVO("Nuevo"),
    REACONDICIONADO("Reacondicionado"),
    USADO("Usado");

    private final String descripcion;

    EstadoEquipo(String descripcion) {
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
