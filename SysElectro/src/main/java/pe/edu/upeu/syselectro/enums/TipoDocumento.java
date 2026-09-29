package pe.edu.upeu.syselectro.enums;

public enum TipoDocumento {
    DNI("DNI"),
    CE("Carné de extranjería"),
    RUC("RUC"),
    PASAPORTE("Pasaporte");

    private final String descripcion;

    TipoDocumento(String descripcion) {
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
