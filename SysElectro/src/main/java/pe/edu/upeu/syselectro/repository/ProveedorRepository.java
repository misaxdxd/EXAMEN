package pe.edu.upeu.syselectro.repository;

import pe.edu.upeu.syselectro.model.Proveedor;

public class ProveedorRepository extends AbstractJpaRepository<Proveedor, Long> {

    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Proveedor entity) {
        return entity.getIdProveedor();
    }

    @Override
    protected void setId(Proveedor entity, Long id) {
        entity.setIdProveedor(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Carga datos de ejemplo una sola vez (no se vuelve a sembrar aunque se borre todo). */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(Proveedor.builder()
                .ruc("20601234567")
                .razonSocial("Distribuidora TecnoPerú S.A.C.")
                .telefono("999111222")
                .email("contacto@tecnoperu.pe")
                .direccion("Av. Industrial 800, Lima")
                .build());
        save(Proveedor.builder()
                .ruc("20605554443")
                .razonSocial("Importaciones Electro Andina S.A.C.")
                .telefono("988777666")
                .email("ventas@electroandina.pe")
                .direccion("Calle Comercio 55, Arequipa")
                .build());
    }
}
