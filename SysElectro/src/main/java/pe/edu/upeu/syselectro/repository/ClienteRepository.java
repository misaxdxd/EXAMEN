package pe.edu.upeu.syselectro.repository;

import pe.edu.upeu.syselectro.enums.TipoDocumento;
import pe.edu.upeu.syselectro.model.Cliente;

public class ClienteRepository extends AbstractJpaRepository<Cliente, Long> {

    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Cliente entity) {
        return entity.getIdCliente();
    }

    @Override
    protected void setId(Cliente entity, Long id) {
        entity.setIdCliente(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Carga datos de ejemplo una sola vez (no se vuelve a sembrar aunque se borre todo). */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(Cliente.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("45871236")
                .nombres("María Torres Quispe")
                .telefono("987654321")
                .email("maria.torres@correo.com")
                .direccion("Av. Arequipa 450, Lima")
                .build());
        save(Cliente.builder()
                .tipoDocumento(TipoDocumento.RUC)
                .numeroDocumento("20512345678")
                .nombres("Comercial Los Andes S.A.C.")
                .telefono("912345678")
                .email("ventas@losandes.pe")
                .direccion("Jr. Puno 210, Juliaca")
                .build());
    }
}
